package com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.dialogs

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.diches.dichboxmobile.api.Statuses
import com.diches.dichboxmobile.api.boxes.BoxesAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.FilesListViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.OpenedFilesViewModel
import com.diches.dichboxmobile.mv.settings.ConfirmDialog
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoveFileDialog(
        private val context: Context,
        states: List<ViewModel>
) {
    private val filesListVM = states[0] as FilesListViewModel
    private val userStateVM = states[1] as UserStateViewModel
    private val boxDataVM = states[2] as BoxDataViewModel
    private val openedFilesVM = states[3] as OpenedFilesViewModel
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
            println(userStateVM.namesState.value)
            val users = userStateVM.namesState.value!!
            path.add(0, users.second!!)
            val rmBody = BoxesContainer.FilePropertiesReq(
                    boxPath = path,
                    viewerName = users.first!!,
                    fileName = name,
                    type = type
            )
            val (st, res) = withContext(Dispatchers.IO) { api.removeFile(rmBody) }
            if (Statuses.OK.eqNot(st)) return@launch
            val (_, edited) = res as BoxesContainer.RemoveFileRes
            val dataEdited = boxDataVM.liveData.value!!.copy(last_edited = edited)
            boxDataVM.setBoxData(dataEdited)
            val currentEntries = filesListVM.liveData.value!!.entries
            val currentFiles = currentEntries.dir!!
            val filesEdited = currentFiles.src.filter { it.name != name }
            val entriesEdited = currentEntries.copy(dir = currentFiles.copy(src = filesEdited))
            filesListVM.setFilesList(BoxesContainer.PathEntries(entriesEdited))

            if (openedFilesVM.liveData.value != null) {
                path.add(name)
                val fullPath = path.joinToString("/")
                openedFilesVM.closeByPath(fullPath)
            }
        }
    }
}