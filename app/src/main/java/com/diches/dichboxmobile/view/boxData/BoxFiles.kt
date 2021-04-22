package com.diches.dichboxmobile.view.boxData

import android.content.Intent
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.EditorTools
import com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.EntriesManipulator
import com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.dialogs.InputDialog
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.FilesListViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.tools.fromBitmapToBase64
import com.diches.dichboxmobile.tools.fromUriToBitmap
import java.io.File

class BoxFiles : Fragment() {
    private lateinit var entriesManipulator: EntriesManipulator
    private lateinit var editorTools: EditorTools
    private var editor = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_box_files, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernamesVM = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val boxDataVM = ViewModelProvider(requireActivity()).get(BoxDataViewModel::class.java)
        val filesListVM = ViewModelProvider(requireActivity()).get(FilesListViewModel::class.java)

        val pathDepth = view.findViewById<TextView>(R.id.boxFilesPath)
        val goBackView = view.findViewById<LinearLayout>(R.id.goBackOption)
        val filesList = view.findViewById<ListView>(R.id.boxFilesList)
        val emptyList = view.findViewById<TextView>(R.id.emptyFilesList)
        val search = view.findViewById<EditText>(R.id.boxesFilesSearch)
        val addFilesView = view.findViewById<LinearLayout>(R.id.addBoxFiles)
        val addFileIcon = view.findViewById<ImageView>(R.id.addFileIcon)
        val addImgIcon = view.findViewById<ImageView>(R.id.addImgIcon)
        val addDirIcon = view.findViewById<ImageView>(R.id.addDirIcon)

        editor = boxDataVM.liveData.value!!.editor
        addFilesView.isVisible = editor
        filesList.emptyView = emptyList

        val names = usernamesVM.namesState.value!!
        val viewerName = names.first

        val states = listOf(filesListVM, boxDataVM, usernamesVM)
        entriesManipulator = EntriesManipulator(this, states, viewerName!!)
                .fillFilesState()
                .setPathDepth(pathDepth, savedInstanceState)
                .addRemoveFileDialog()
                .addListAdapter(filesList)
                .handleSearch(search, emptyList)
                .setGoBackView(goBackView)

        if (editor) {
            editorTools = EditorTools(this, filesListVM).addImage(addImgIcon)
            entriesManipulator.handleEntriesAddition(editorTools, Pair(addFileIcon, addDirIcon))
        }

        savedInstanceState?.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        entriesManipulator.saveState(outState)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (editor)
            editorTools.handleActivityResult(requestCode, resultCode, data) { type, name, src ->
                entriesManipulator.addNewFile(type, name, src)
            }
    }
}