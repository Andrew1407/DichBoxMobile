package com.diches.dichboxmobile.mv.verifiers

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.AppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FieldsVerifier <K> (
        private val inputVerifier: InputVerifier<K>,
        private val submitter: Button
) {
    private val fieldsEntries: MutableMap<K, String> = mutableMapOf()

    init {
        submitter.isEnabled = false
    }

    fun getInput(): Map<K, String> = fieldsEntries.toMap()

    fun addSubmitListener(fn: suspend () -> Unit): FieldsVerifier<K> {
        submitter.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                fn()
            }
        }
        return this
    }

    fun addInputEntry(key: K, input: String = ""): FieldsVerifier<K> {
        fieldsEntries[key] = input
        return this
    }

    fun onInputCheck(
            key: K,
            input: EditText,
            warning: TextView
    ) {
        input.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {
                val inputText = input.text.toString()
                val rotated = fieldsEntries[key] == inputText
                if (rotated) return@launch

                val warningText = withContext(Dispatchers.IO) {
                    inputVerifier.verify(key, inputText)
                }
                fieldsEntries[key] = inputText
                decorateFields(warningText, input, warning)
                checkAllAdded()
            }
        }
    }

    private fun decorateFields(
            warnMessage: String,
            input: EditText,
            warning: TextView
    ) {
        val hasWarning = warnMessage.isNotEmpty()
        val warnColor = if (hasWarning) AppColors.CRIMSON else AppColors.GREEN
        warning.text = warnMessage
        DrawableCompat.setTint(input.background, warnColor.raw)
    }

    private fun checkAllAdded() {
        val isValid = inputVerifier.checkAll()
        submitter.isEnabled = isValid
        val btnColor = if (isValid) AppColors.GREEN else AppColors.BLUE
        val btnBackground = if (isValid) R.drawable.sign_submit_btn_active
            else R.drawable.sign_submit_btn_inactive
        submitter.setTextColor(btnColor.raw)
        submitter.setBackgroundResource(btnBackground)
    }
}