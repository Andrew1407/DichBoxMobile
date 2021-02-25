package com.diches.dichboxmobile.mv

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class SignVerifier(
        private val submitBtn: Button,
        protected val fieldsValid: MutableMap<SignFields, String>,
        protected val verifiers: MutableMap<SignFields, Regex>
) {
    protected val api = UserAPI()
    private lateinit var saveHandler: (String) -> Unit

    init {
        submitBtn.isEnabled = false
        submitBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                handleSubmit(saveHandler)
            }
        }
    }

    fun setSaveHandler(fn: (String) -> Unit) {
        saveHandler = fn
    }

    protected suspend fun verifyField(
            field: String,
            value: String
    ): Boolean {
        val container = UserContainer.VerifyField(field, value)
        val (_, res) = api.verifyField(container)
        return (res as UserContainer.VerifyFieldRes).foundValue != null
    }

    protected fun checkFieldTemplate(
            field: SignFields,
            input: String
    ): Boolean = verifiers[field]!!.matches(input)

    protected fun handleInput(
            field: SignFields,
            input: EditText,
            warning: TextView,
            handler: suspend (String) -> String
    ) {
        input.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {
                val inputText = input.text.toString()
                val green = -0xff00b4
                val warningMsg = withContext(Dispatchers.IO) {
                    handler(inputText)
                }
                val hasWarning = warningMsg.isNotEmpty()
                val crimson = -0x23ebc4
                warning.text = warningMsg
                fieldsValid[field] = if (hasWarning) "" else inputText
                DrawableCompat.setTint(input.background, if (hasWarning) crimson else green)
                handleSubmitBtn()
            }
        }
    }

    private fun handleSubmitBtn() {
        val isValid = fieldsValid.values
                .map { it.isNotEmpty() }
                .reduce { acc, b -> acc && b }
        submitBtn.isEnabled = isValid
        val blue = -0xff2601
        val green = -0xff00b4
        submitBtn.setTextColor(if (isValid) green else blue)
        submitBtn.setBackgroundResource(if (isValid) R.drawable.sign_submit_btn_active else R.drawable.sign_submit_btn_inactive)
    }

    protected abstract suspend fun handleSubmit(fn: (String) -> Unit)
}