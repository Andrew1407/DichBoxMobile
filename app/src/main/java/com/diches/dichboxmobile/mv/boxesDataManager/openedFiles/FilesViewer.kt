package com.diches.dichboxmobile.mv.boxesDataManager.openedFiles

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.OpenedFilesViewModel

class FilesViewer(
        private val fragment: Fragment,
        private val openedFilesVM: OpenedFilesViewModel,
        private val toolbar: FileToolbar
) {
    private var initial = true
    private var savedFilesState = openedFilesVM.liveData.value!!.toList()

    fun addListAdapter(recyclerView: RecyclerView): FilesViewer {
        recyclerView.layoutManager = LinearLayoutManager(
                fragment.requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        )
        recyclerView.adapter = TabsAdapter(openedFilesVM)

        // on tabs changed observer
        openedFilesVM.liveData.observe(fragment.viewLifecycleOwner) {
            if (it == null || !checkFilesChanged(it)) return@observe
            (recyclerView.adapter as TabsAdapter).notifyDataSetChanged()
            toolbar.onOpenFile()
            savedFilesState = it.toList()
            initial = false
        }

        return this
    }

    private fun checkFilesChanged(files: List<BoxesContainer.OpenedFile>): Boolean {
        if (files.size != savedFilesState.size || initial) return true
        for ((i, file) in files.withIndex()) {
            val savedFile = savedFilesState[i]
            val fileChanged = savedFile.name != file.name ||
                    savedFile.filePath != file.filePath ||
                    savedFile.opened != file.opened
            if (fileChanged) return true
        }
        return false
    }
}
