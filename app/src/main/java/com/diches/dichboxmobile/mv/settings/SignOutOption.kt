package com.diches.dichboxmobile.mv.settings

import android.content.Context
import android.widget.TextView
import com.diches.dichboxmobile.mv.boxesDataManager.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel

open class SignOutOption(
        private val context: Context,
        userState: Pair<UserStateViewModel, UserDataViewModel>,
        boxState: Pair<CurrentBoxViewModel, BoxDataViewModel>
) {
    private val dialog = ConfirmDialog()
    private val userStateViewModel = userState.first
    private val userViewModel = userState.second
    private val boxStateViewModel = boxState.first
    private val boxViewModel = boxState.second

    fun handleOptionAction(btn: TextView, title: String) {
        btn.setOnClickListener {
            dialog.buildDialog(context, title) {
                onOkClick()
            }
        }
    }

    protected open fun onOkClick() {
        val signedFile = context.getFileStreamPath("signed.txt")
        if (!signedFile!!.exists()) return
        userStateViewModel.setState(Pair(null, null))
        userViewModel.setUserData(null)
        if (boxStateViewModel.boxName.value != null) boxStateViewModel.setCurrentBox(null)
        if (boxViewModel.liveData.value != null) boxViewModel.setBoxData(null)
        signedFile.delete()
    }
}