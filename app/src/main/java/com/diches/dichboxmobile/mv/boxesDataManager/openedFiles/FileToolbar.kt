package com.diches.dichboxmobile.mv.boxesDataManager.openedFiles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.boxes.BoxesAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.OpenedFilesViewModel
import com.diches.dichboxmobile.tools.fromBase64ToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FileToolbar(
        private val fragment: Fragment,
        bundle: Bundle?,
        private val username: String?,
        states: Pair<OpenedFilesViewModel, BoxDataViewModel>
) {
    private val api = BoxesAPI()
    private var viewModeSet = bundle?.getBoolean("viewMode") ?: true
    private val openedFilesVM = states.first
    private val boxDataVM = states.second

    private lateinit var imageArea: ImageView
    private lateinit var textArea: EditText

    private lateinit var viewMode: ImageView
    private lateinit var saveFile: ImageView
    private lateinit var saveAll: ImageView
    private lateinit var zoomIn: ImageView
    private lateinit var zoomOut: ImageView
    private lateinit var copyToClipboard: ImageView

    fun handleViewMode(view: ImageView): FileToolbar {
        viewMode = view
        val editor = boxDataVM.liveData.value!!.editor
        if (!editor) {
            viewMode.visibility = View.GONE
        } else {
            val iconRes = if (viewModeSet) R.drawable.file_edit else R.drawable.file_edit_cancel
            viewMode.setImageResource(iconRes)
        }
        viewMode.setOnClickListener {
            viewModeSet = !viewModeSet
            val icon = if (viewModeSet) R.drawable.file_edit else R.drawable.file_edit_cancel
            textArea.isEnabled = !viewModeSet
            viewMode.setImageResource(icon)
            showMessage(if (viewModeSet) "View mode set" else "Edit mode set")
            if (viewModeSet) {
                openedFilesVM.setViewMode()
                val openedFile = getOpenedFile()
                textArea.setText(openedFile.src)
            }
        }
        return this
    }

    fun handleSaveFile(view: ImageView): FileToolbar {
        saveFile = view
        val editor = boxDataVM.liveData.value!!.editor
        if (!editor) saveFile.visibility = View.GONE
        saveFile.setOnClickListener { saveEditedFiles(openedOnly = true) }
        return this
    }

    fun handleSaveAll(view: ImageView): FileToolbar {
        saveAll = view
        val editor = boxDataVM.liveData.value!!.editor
        if (!editor) saveAll.visibility = View.GONE
        saveAll.setOnClickListener { saveEditedFiles(openedOnly = false) }
        return this
    }

    private fun saveEditedFiles(openedOnly: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            val editedFiles = openedFilesVM.liveData.value!!.filter {
                val edited = it.edited != null
                if (openedOnly) it.opened && edited else edited
            }
            if (editedFiles.isEmpty()) return@launch
            val editedEntries = editedFiles.map {
                val path = "${it.filePath}/${it.name}"
                BoxesContainer.SaveFilesReq.Files(it.edited!!, path)
            }
            val saveContainer = BoxesContainer.SaveFilesReq(username!!, editedEntries)
            val (st, res) = withContext(Dispatchers.IO) { api.saveFiles(saveContainer) }
            if (st == 200) {
                openedFilesVM.writeFiles(openedOnly)
                val message = if (openedOnly) "File \"${editedFiles[0].name}\" has been saved"
                    else "All edited files have been saved"
                val (_, lastEdited) = res as BoxesContainer.SaveFilesRes
                val dataEdited = boxDataVM.liveData.value!!.copy(last_edited = lastEdited)
                boxDataVM.setBoxData(dataEdited)
                showMessage(message)
            }
        }
    }

    fun handleZoomIn(view: ImageView): FileToolbar {
        zoomIn = view
        zoomIn.setOnClickListener { handleZoom(increase = true) }
        return this
    }

    fun handleZoomOut(view: ImageView): FileToolbar {
        zoomOut = view
        zoomOut.setOnClickListener { handleZoom(increase = false) }
        return this
    }

    fun handleClipboard(view: ImageView): FileToolbar {
        copyToClipboard = view
        copyToClipboard.setOnClickListener {
            val clipboard = fragment
                    .requireActivity()
                    .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val textToCopy = textArea.text.toString()
            val clip = ClipData.newPlainText("RANDOM UUID", textToCopy)
            clipboard.setPrimaryClip(clip)
            val filename = getOpenedFile().name
            val message = "File \"$filename\" copied to clipboard"
            showMessage(message)
        }
        return this
    }

    fun setState(bundle: Bundle) = bundle.putBoolean("viewMode", viewModeSet)

    fun handleEditFields(imgArea: ImageView, txtArea: EditText): FileToolbar {
        imageArea = imgArea
        textArea = txtArea
        if (textArea.isEnabled == viewModeSet) textArea.isEnabled = !viewModeSet
        textArea.addTextChangedListener {
            val openedFile = getOpenedFile()
            val input = textArea.text.toString()
            openedFilesVM.editFile(openedFile.name, openedFile.filePath, input)
        }
        return this
    }

    fun onOpenFile() {
        val openedFile = getOpenedFile()
        val isImage = openedFile.type == "image"
        imageArea.isVisible = isImage
        textArea.isVisible = !isImage
        manageIcons(!isImage)
        if (isImage) {
            val bitmapSrc = fromBase64ToBitmap(openedFile.src)
            imageArea.setImageBitmap(bitmapSrc)
        } else {
            val textSrc = openedFile.edited ?: openedFile.src
            textArea.setText(textSrc)
        }
    }

    private fun showMessage(text: String) {
        Toast.makeText(fragment.requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun getOpenedFile(): BoxesContainer.OpenedFile {
        val openedList = openedFilesVM.liveData.value!!
        return openedList.first { it.opened }
    }

    private fun manageIcons(isFile: Boolean) {
        val editor = boxDataVM.liveData.value!!.editor
        if (editor) {
            viewMode.isVisible = isFile
            saveAll.isVisible = isFile
            saveFile.isVisible = isFile
        }
        zoomOut.isVisible = isFile
        zoomIn.isVisible = isFile
        copyToClipboard.isVisible = isFile
    }

    private fun handleZoom(increase: Boolean) {
        val currentSizePixels = textArea.textSize
        val currentSize = currentSizePixels / fragment.resources.displayMetrics.scaledDensity
        val accumulated = if (increase) currentSize + 2f else currentSize - 2f
        if (accumulated > 36 || accumulated < 16) return
        textArea.setTextSize(TypedValue.COMPLEX_UNIT_SP, accumulated)
    }

}
