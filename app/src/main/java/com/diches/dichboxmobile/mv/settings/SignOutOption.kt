package com.diches.dichboxmobile.mv.settings

import android.content.Context
import android.widget.TextView
import com.diches.dichboxmobile.mv.userDataManager.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.UserStateViewModel

open class SignOutOption(
        private val context: Context,
        private val userStateViewModel: UserStateViewModel,
        private val userViewModel: UserDataViewModel,
) {
    private val dialog = ConfirmDialog()

    fun handleOptionAction(btn: TextView, title: String) {
        btn.setOnClickListener {
            dialog.buildDialog(context, title) {
                onOkClick()
            }
        }
    }

    protected open fun onOkClick() {
        val signedFile = context.getFileStreamPath("signed.txt")
        if (signedFile!!.exists()) {
            userStateViewModel.setState(Pair(null, null))
            userViewModel.setUserData(null)
            signedFile.delete()
        }
    }
}