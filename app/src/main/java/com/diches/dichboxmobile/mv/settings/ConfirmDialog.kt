package com.diches.dichboxmobile.mv.settings

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.diches.dichboxmobile.R

class ConfirmDialog {
    fun buildDialog(ctx: Context, title: String, okClb: () -> Unit): AlertDialog {
        val dialog = AlertDialog.Builder(ctx, R.style.dialogTheme)
                .setMessage("¿ $title ؟")
                .setPositiveButton("ok") { _, _ -> okClb() }
                .setNegativeButton("cancel") { dialog, _ -> dialog.cancel() }
                .show()

        decorateDialog(dialog)
        return dialog
    }

    private fun decorateDialog(dialog: AlertDialog) {
        val msg = dialog.findViewById<TextView>(android.R.id.message)
        val btnPositive = dialog.findViewById<Button>(android.R.id.button1)
        val btnNegative = dialog.findViewById<Button>(android.R.id.button2)
        (msg.layoutParams as LinearLayout.LayoutParams).bottomMargin = 150
        msg.textSize = 25f
        msg.gravity = Gravity.CENTER_HORIZONTAL
        btnPositive.setBackgroundResource(R.drawable.color_picker)
        btnNegative.setBackgroundResource(R.drawable.color_picker)
        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 3f
        layoutParams.rightMargin = 60
        layoutParams.topMargin = 40
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }
}