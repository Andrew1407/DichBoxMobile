package com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.*
import androidx.core.text.set
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.boxes.BoxesAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.dialogs.InputDialog
import com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.dialogs.RemoveFileDialog
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.FileRedirectorViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.FilesListViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.OpenedFilesViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.tools.AppColors
import kotlinx.coroutines.*

class EntriesManipulator(
        private val fragment: Fragment,
        states: List<ViewModel>,
        private val viewerName: String
) {
    private val api = BoxesAPI()
    private val filesListVM = states[0] as FilesListViewModel
    private val boxDataVM = states[1] as BoxDataViewModel
    private val userStateVM = states[2] as UserStateViewModel
    private val openedFilesVM = states[3] as OpenedFilesViewModel
    private val redirectorVM = states[4] as FileRedirectorViewModel
    private lateinit var pathDepthView: TextView
    private lateinit var listView: ListView
    private lateinit var goBackView: LinearLayout
    private lateinit var searchView: EditText
    private lateinit var removeFileDialog: RemoveFileDialog

    fun fillFilesState(): EntriesManipulator {
        if (filesListVM.liveData.value != null) return this
        val boxData = boxDataVM.liveData.value!!
        val boxPath = listOf(boxData.owner_name, boxData.name)
        val pathBody = BoxesContainer.PathEntriesReq(boxPath, viewerName, true)
        val (st, res) = runBlocking { api.getPathFiles(pathBody) }
        filesListVM.setFilesList(res as BoxesContainer.PathEntries)
        return this
    }

    fun handleBoxChanges(): EntriesManipulator {
        boxDataVM.liveData.observe(fragment.viewLifecycleOwner) {
            val pathDepthStr = pathDepthView.text.toString()
            if (it == null || pathDepthStr.isEmpty()) return@observe
            val pathEntries = pathDepthStr.split(" / ").toMutableList()
            if (pathEntries[0] == it.name) return@observe
            pathEntries[0] = it.name
            configurePath(pathEntries.joinToString(" / "))
        }
        return this
    }

    fun addRemoveFileDialog(): EntriesManipulator {
        val states = listOf(filesListVM, userStateVM, boxDataVM, openedFilesVM)
        removeFileDialog = RemoveFileDialog(fragment.requireContext(), states)
        return this
    }

    fun addListAdapter(filesListView: ListView): EntriesManipulator {
        listView = filesListView
        val entries = filesListVM.liveData.value!!.entries.dir!!.src
        val editor = boxDataVM.liveData.value!!.editor
        listView.adapter = FilesListAdapter(
            fragment.requireContext(), R.layout.files_list_item,
            entries, removeFileDialog, editor
        ).handleRedirection { name, type ->
            val size = pathDepthView.text.toString().split(" / ").size
            if (type == "dir") redirectDir(name, size)
            else openFile(name, type)
        }
            .handleRemove { name, type -> removeFile(name, type) }
            .handleRename { name, type -> renameFile(name, type) }

        observeFilesChanges()

        return this
    }

    fun handleEntriesAddition(editorTools: EditorTools, views: Pair<View, View>): EntriesManipulator {
        val addFileIcon = views.first
        val addDirIcon = views.second
        editorTools.addFile(addFileIcon, "Add file") { addNewFile("file", it) }
        editorTools.addFile(addDirIcon, "Add directory") { addNewFile("dir", it) }
        return this
    }

    private fun openFile(name: String, type: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val usernames = userStateVM.namesState.value!!
            val viewer = usernames.first!!
            val owner = usernames.second!!
            val path = pathDepthView.text.toString().split(" / ").toMutableList()
            path.add(0, owner)
            val getFleBody = BoxesContainer.FilePropertiesReq(
                    boxPath = path,
                    viewerName = viewer,
                    fileName = name,
                    type = type
            )

            val (st, res) = withContext(Dispatchers.IO) { api.getFileEntries(getFleBody) }
            if (st != 200) return@launch
            val (_, foundData) = res as BoxesContainer.FoundFile
            val openedFile = BoxesContainer.OpenedFile(
                    opened = true,
                    name = name,
                    filePath = '/' + path.joinToString(separator = "/"),
                    src = foundData,
                    type = type
            )
            goToOpenedFiles(openedFile)
            redirectorVM.setRedirected(true)
        }
    }

    private fun goToOpenedFiles(openedFile: BoxesContainer.OpenedFile) {
        val openedFiles = openedFilesVM.liveData.value ?: return openedFilesVM.addNewFile(openedFile)
        for ((i, file) in openedFiles.withIndex()) {
            val found = openedFile.name == file.name && openedFile.filePath == file.filePath
            if (found) return if (!file.opened) openedFilesVM.openFile(i) else Unit
        }
        openedFilesVM.addNewFile(openedFile)
    }

    private fun observeFilesChanges() {
        filesListVM.liveData.observe(fragment.viewLifecycleOwner) {
            if (it == null) return@observe
            val adapter = (listView.adapter as FilesListAdapter)
            val oldEntries = adapter.items
            val newEntries = it.entries.dir!!.src
            if (oldEntries == newEntries) return@observe
            adapter.items = newEntries.toList()
            adapter.itemsShown = newEntries
            searchView.text = searchView.text
        }
    }

    private fun redirectDir(name: String, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val pathDepth = pathDepthView.text.toString()
            val owner = boxDataVM.liveData.value!!.owner_name
            var boxPath = pathDepth.split(" / ").toMutableList()
            if (position < boxPath.size) boxPath = boxPath.slice(0..position).toMutableList()
            else boxPath.add(name)
            boxPath.add(0, owner)
            val pathBody = BoxesContainer.PathEntriesReq(boxPath, viewerName, true)
            val (st, res) = withContext(Dispatchers.IO) { api.getPathFiles(pathBody) }
            if (st == 200) {
                val newDir = res as BoxesContainer.PathEntries
                val entries = newDir.entries.dir!!.src
                val adapter = listView.adapter as FilesListAdapter
                filesListVM.setFilesList(newDir)
                adapter.items = entries.toList()
                adapter.itemsShown = entries
                adapter.notifyDataSetChanged()
                searchView.text.clear()
                val newPath = boxPath.toList()
                    .subList(1, boxPath.size)
                    .joinToString(separator = " / ")
                configurePath(newPath)
                goBackView.isVisible = boxPath.size > 2
            }
        }
    }

    private fun renameFile(name: String, type: String) {
        val fullPath = pathDepthView.text.toString().replace(" ", "")
        val title = "Rename $type \"$name ($fullPath/$name)\""
        InputDialog(filesListVM).buildDialog(fragment, title) { newName ->
            CoroutineScope(Dispatchers.Main).launch {
                val usernames = userStateVM.namesState.value!!
                val viewer = usernames.first!!
                val owner = usernames.second!!
                val path = fullPath.split("/").toMutableList()
                path.add(0, owner)
                val renameContainer = BoxesContainer.RenameFileReq(
                        boxPath = path,
                        viewerName = viewer,
                        fileName = name,
                        newName = newName
                )
                val (st, res) = withContext(Dispatchers.IO) { api.renameFile(renameContainer) }
                if (st != 200) return@launch
                val (_, edited) = res as BoxesContainer.RenameFileRes
                val dataEdited = boxDataVM.liveData.value!!.copy(last_edited = edited)
                boxDataVM.setBoxData(dataEdited)
                val currentEntries = filesListVM.liveData.value!!.entries
                val currentFiles = currentEntries.dir!!
                val filesEdited = currentFiles.src.toMutableList()
                filesEdited.forEachIndexed { index, typeDir ->
                    if (typeDir.name == name) filesEdited[index] = typeDir.copy(name = newName)
                }
                val entriesEdited = currentEntries.copy(dir = currentFiles.copy(src = filesEdited))
                filesListVM.setFilesList(BoxesContainer.PathEntries(entriesEdited))

                val openedFiles = openedFilesVM.liveData.value ?: return@launch
                if (type == "dir") {
                    val oldPath = "/$owner/$fullPath/$name"
                    val newPath = "/$owner/$fullPath/$newName"
                    openedFilesVM.renamePaths(oldPath, newPath)
                } else {
                    openedFiles.forEachIndexed { i, file ->
                        val fPath = file.filePath
                        val filePath = if (fPath.startsWith('/')) fPath else "/$fPath"
                        val found = name == file.name && filePath == "/$owner/$fullPath"
                        if (found) openedFilesVM.renameFile(i, newName)
                    }
                }
            }
        }
    }

    private fun removeFile(name: String, type: String) {
        val filePath = pathDepthView.text.toString().replace(" ", "")
        removeFileDialog.setTitleParams(type, name, filePath)
    }

    fun saveState(bundle: Bundle) {
        bundle.putString("pathDepth", pathDepthView.text.toString())
    }

    fun setPathDepth(view: TextView, bundle: Bundle?): EntriesManipulator {
        pathDepthView = view
        val initialPath = if (bundle == null) boxDataVM.liveData.value!!.name
            else bundle.getString("pathDepth")!!
        pathDepthView.movementMethod = LinkMovementMethod()
        configurePath(initialPath)
        return this
    }

    fun handleSearch(search: EditText, emptyListView: TextView): EntriesManipulator {
        searchView = search
        searchView.addTextChangedListener {
            val input = searchView.text.toString()
            handleEmptyView(emptyListView, input.isEmpty())
            (listView.adapter as Filterable).filter.filter(input)
        }

        return this
    }

    fun setGoBackView(view: LinearLayout): EntriesManipulator {
        goBackView = view
        val pathLength = pathDepthView
            .text.toString()
            .split(" / ").size
        goBackView.isVisible = pathLength > 1
        goBackView.setOnClickListener {
            val lastIndex = pathDepthView.text.toString().split(" / ").lastIndex
            redirectDir("", lastIndex - 1)
        }

        return this
    }

    private fun handleEmptyView(view: TextView, inputEmpty: Boolean) {
        val emptyMsg = "This directory is empty"
        val foundNoneMsg = "No files or directories were found"
        if (inputEmpty && view.text != emptyMsg) view.text = emptyMsg
        else if (!inputEmpty && view.text != foundNoneMsg) view.text = foundNoneMsg
    }

    private fun configurePath(path: String) {
        val pathArr = path.split(" / ")
        val spannable = SpannableString(path)
        val nameColor = boxDataVM.liveData.value!!.name_color
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor(nameColor)),
            0, pathArr[0].length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0, pathArr[0].length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        if (pathArr.size > 1)
             for (i in 0 until pathArr.lastIndex) {
                 val newPath = pathArr.subList(0, i + 1).joinToString(separator = " / ")
                 val lastDir = pathArr[i]
                 val lastIndex = newPath.length
                 val startIndex = lastIndex - lastDir.length
                 spannable[startIndex..lastIndex] = object : ClickableSpan() {
                     override fun onClick(widget: View) = redirectDir(lastDir, i)
                     override fun updateDrawState(ds: TextPaint) {
                         if (i != 0) ds.color = AppColors.BLUE.raw
                         ds.isUnderlineText = false
                     }
                 }
             }

        pathDepthView.text = spannable
    }

    fun addNewFile(type: String, name: String, src: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            val usernames = userStateVM.namesState.value!!
            val viewer = usernames.first!!
            val owner = usernames.second!!
            val path = pathDepthView.text.toString().split(" / ").toMutableList()
            path.add(0, owner)
            val entries = if (src == null) null else {
                val format = name.split(".").last()
                "data:image/$format;base64,$src"
            }

            val createContainer = BoxesContainer.FilePropertiesReq(
                    boxPath = path,
                    viewerName = viewer,
                    fileName = name,
                    type = type,
                    src = entries
            )
            val (st, res) = withContext(Dispatchers.IO) { api.createFile(createContainer) }
            if (st != 201) return@launch
            val (newList, editedDate) = res as BoxesContainer.CreateFileRes
            val currentEntries = filesListVM.liveData.value!!.entries
            val currentDir = currentEntries.dir!!
            val newEntries = if (type == "dir") newList.dir!! else newList.file!!
            val dataEdited = boxDataVM.liveData.value!!.copy(last_edited = editedDate)
            boxDataVM.setBoxData(dataEdited)
            val newFiles = currentEntries.copy(dir = currentDir.copy(src = newEntries.src))
            val entriesEdited = BoxesContainer.PathEntries(newFiles)
            filesListVM.setFilesList(entriesEdited)

            if (type == "dir") return@launch
            val openedFile = BoxesContainer.OpenedFile(
                opened = true,
                name = name,
                filePath = '/' + path.joinToString(separator = "/"),
                src = src ?: "",
                type = type
            )
            openedFilesVM.addNewFile(openedFile)
            redirectorVM.setRedirected(true)
        }
    }
}