package com.diches.dichboxmobile.view.boxData.openedFiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.openedFiles.FileToolbar
import com.diches.dichboxmobile.mv.boxesDataManager.openedFiles.FilesViewer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.OpenedFilesViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel

class OpenedFilesList : Fragment() {
    private lateinit var fileToolbar: FileToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_box_opened_files, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boxDataVM = ViewModelProvider(requireActivity()).get(BoxDataViewModel::class.java)
        val openedFilesVM = ViewModelProvider(requireActivity()).get(OpenedFilesViewModel::class.java)
        val usernamesVM = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)

        val clipboardIcon = view.findViewById<ImageView>(R.id.clipboardBtn)
        val zoomOutIcon = view.findViewById<ImageView>(R.id.zoomOutBtn)
        val zoomInIcon = view.findViewById<ImageView>(R.id.zoomInBtn)
        val saveAllIcon = view.findViewById<ImageView>(R.id.saveAllFileBtn)
        val saveFileIcon = view.findViewById<ImageView>(R.id.saveFileBtn)
        val editModeIcon = view.findViewById<ImageView>(R.id.setEditModeBtn)

        val textArea = view.findViewById<EditText>(R.id.fileEditorArea)
        val imageArea = view.findViewById<ImageView>(R.id.imageEditorArea)
        val recyclerView = view.findViewById<RecyclerView>(R.id.openedFilesList)

        val states = Pair(openedFilesVM, boxDataVM)
        val viewer = usernamesVM.namesState.value!!.first
        fileToolbar = FileToolbar(this, savedInstanceState, viewer, states)
                .handleEditFields(imageArea, textArea)
                .handleClipboard(clipboardIcon)
                .handleSaveFile(saveFileIcon)
                .handleSaveAll(saveAllIcon)
                .handleViewMode(editModeIcon)
                .handleZoomIn(zoomInIcon)
                .handleZoomOut(zoomOutIcon)

        val filesViewer = FilesViewer(this, openedFilesVM, fileToolbar)
            .addListAdapter(recyclerView)

        savedInstanceState?.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fileToolbar.setState(outState)
    }
}