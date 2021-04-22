package com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.dialogs

import android.content.Context
import android.view.View
import com.diches.dichboxmobile.api.boxes.BoxesAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.FilesListViewModel
import com.diches.dichboxmobile.mv.settings.ConfirmDialog
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoveFileDialog(
        private val context: Context,
        private val filesListVM: FilesListViewModel,
        private val userStateVM: UserStateViewModel,
        private val boxDataVM: BoxDataViewModel
) {
    private val api = BoxesAPI()
    private val dialog = ConfirmDialog()
    private var fullPath = ""
    private var type = ""
    private var name = ""

    fun setTitleParams(fileType: String, filename: String, path: String) {
        type = fileType
        fullPath = path
        name = filename
    }

    fun handleOptionAction(view: View, titleHandler: () -> Unit) {
        view.setOnClickListener {
            titleHandler()
            val title = "Remove $type \"$name ($fullPath/$name)\""
            dialog.buildDialog(context, title) { onOkClick() }
        }
    }

    private fun onOkClick() {
        CoroutineScope(Dispatchers.Main).launch {
            val path = fullPath.split("/").toMutableList()
            val users = userStateVM.namesState.value!!
            path.add(0, users.second!!)
            val rmBody = BoxesContainer.FilePropertiesReq(
                    boxPath = path,
                    viewerName = users.first!!,
                    fileName = name,
                    type = type
            )
            val (st, res) = withContext(Dispatchers.IO) { api.removeFile(rmBody) }
            if (st != 200) return@launch
            val (_, edited) = res as BoxesContainer.RemoveFileRes
            val dataEdited = boxDataVM.liveData.value!!.copy(last_edited = edited)
            boxDataVM.setBoxData(dataEdited)
            val currentEntries = filesListVM.liveData.value!!.entries
            val currentFiles = currentEntries.dir!!
            val filesEdited = currentFiles.src.filter { it.name != name }
            val entriesEdited = currentEntries.copy(dir = currentFiles.copy(src = filesEdited))
            filesListVM.setFilesList(BoxesContainer.PathEntries(entriesEdited))
        }
    }
}