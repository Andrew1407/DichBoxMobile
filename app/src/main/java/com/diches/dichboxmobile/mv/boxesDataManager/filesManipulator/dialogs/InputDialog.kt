package com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.dialogs

import android.app.AlertDialog
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.FilesListViewModel
import com.diches.dichboxmobile.tools.AppColors
import org.w3c.dom.Text

class InputDialog(private val filesListVM: FilesListViewModel) {
    private lateinit var input: EditText
    private lateinit var warning: TextView
    private lateinit var dialog: AlertDialog

    fun buildDialog(fragment: Fragment, title: String, okClb: (newName: String) -> Unit): AlertDialog {
        val dialogLayout = fragment.layoutInflater.inflate(R.layout.input_dialog, null)
        input = dialogLayout.findViewById(R.id.dialogInput)
        warning = dialogLayout.findViewById(R.id.inputDialogWarning)
        dialog = AlertDialog.Builder(fragment.requireContext(), R.style.dialogTheme)
                .setMessage(title)
                .setView(dialogLayout)
                .setPositiveButton("ok") { _, _ -> okClb(input.text.toString()) }
                .setNegativeButton("cancel") { dialog, _ -> dialog.cancel() }
                .show()

        decorateDialog()
        handleInput()

        return dialog
    }

    private fun handleInput() {
        input.addTextChangedListener {
            val inputStr = input.text.toString()
            val inputValid = Regex("""[^\s/]{1,40}""")
            val okBtn = dialog.findViewById<Button>(android.R.id.button1)
            if (!inputValid.matches(inputStr)) {
                okBtn.isEnabled = false
                DrawableCompat.setTint(input.background, AppColors.CRIMSON.raw)
                val warningText =
                    "Invalid input (name can\\'t include spaces, \"?\", \"/\", \"#\", \"%\", length should be 1-40 symbols)"
                warning.text = warningText
                return@addTextChangedListener
            }
            val entries = filesListVM.liveData.value!!.entries
            val dir = entries.dir!!
            val files = dir.src
            val nameFree = files.none { it.name == inputStr }
            if (!nameFree) {
                okBtn.isEnabled = false
                DrawableCompat.setTint(input.background, AppColors.CRIMSON.raw)
                val warningText = "An entry with the same name already exists in current directory"
                warning.text = warningText
                return@addTextChangedListener
            }
            warning.text = ""
            DrawableCompat.setTint(input.background, AppColors.GREEN.raw)
            okBtn.isEnabled = true
        }
    }

    private fun decorateDialog() {
        val msg = dialog.findViewById<TextView>(android.R.id.message)
        val btnPositive = dialog.findViewById<Button>(android.R.id.button1)
        val btnNegative = dialog.findViewById<Button>(android.R.id.button2)
        (msg.layoutParams as LinearLayout.LayoutParams).bottomMargin = 50
        msg.textSize = 25f
        msg.gravity = Gravity.CENTER_HORIZONTAL
        btnPositive.setBackgroundResource(R.drawable.color_picker)
        btnNegative.setBackgroundResource(R.drawable.color_picker)
        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 3f
        layoutParams.rightMargin = 60
        layoutParams.topMargin = 20
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }
}