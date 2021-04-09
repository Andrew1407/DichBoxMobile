package com.diches.dichboxmobile.mv.boxesDataManager

import android.content.Context
import android.widget.TextView
import com.diches.dichboxmobile.api.boxes.BoxesAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.settings.ConfirmDialog
import com.diches.dichboxmobile.view.boxData.BoxInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RemoveBoxDialog(
        private val context: Context,
        private val currentBoxVM: CurrentBoxViewModel,
        private val boxDataVM: BoxDataViewModel,
        private val redirector: BoxInfo.BoxInfoRedirect
) {
    private val api = BoxesAPI()
    private val dialog = ConfirmDialog()

    fun handleOptionAction(btn: TextView) {
        btn.setOnClickListener {
            val boxName = currentBoxVM.boxName.value
            dialog.buildDialog(context, "Remove box \"$boxName\"") {
                onOkClick()
            }
        }
    }

    private fun onOkClick() {
        CoroutineScope(Dispatchers.Main).launch {
            val boxData = boxDataVM.liveData.value!!
            val rmBody = BoxesContainer.RemoveBoxReq(
                    username = boxData.owner_name,
                    boxName = boxData.name,
                    confirmation = "permitted",
                    ownPage = true
            )
            val (st, res) = api.removeBox(rmBody)
            if (st != 200) return@launch
            val (removed) = res as BoxesContainer.Removed
            if (!removed) return@launch
            currentBoxVM.setCurrentBox(null)
            boxDataVM.setBoxData(null)
            redirector.changeFragmentToBoxesList()
        }
    }
}