package com.diches.dichboxmobile.mv.settings

import android.content.Context
import android.widget.TextView
import com.diches.dichboxmobile.mv.Cleanable

open class SignOutOption(
        private val context: Context,
        private val viewStates: List<Cleanable>
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
        if (!signedFile!!.exists()) return
        viewStates.forEach { it.clear() }
        signedFile.delete()
    }
}