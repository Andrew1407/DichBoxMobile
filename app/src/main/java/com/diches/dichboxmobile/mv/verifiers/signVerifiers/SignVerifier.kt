package com.diches.dichboxmobile.mv.verifiers.signVerifiers

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.datatypes.AppColors
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.verifiers.FieldsVerifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class SignVerifier(private val submitBtn: Button) {
    protected val api = UserAPI()
    private lateinit var saveHandler: (String) -> Unit
    protected abstract val verifier: FieldsVerifier<SignFields>
    protected abstract val templates: MutableMap<SignFields, String>
    protected abstract val fieldsValid: MutableMap<SignFields, String>

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
    ): Boolean = Regex(templates[field]!!).matches(input)

    protected fun handleInput(
            field: SignFields,
            input: EditText,
            warning: TextView
    ) {
        input.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {
                val inputText = input.text.toString()
                val rotated = fieldsValid[field] == inputText
                if (rotated) return@launch

                val warningText = withContext(Dispatchers.IO) {
                    verifier.verify(field, inputText)
                }
                val hasWarning = warningText.isNotEmpty()
                val warnColor = if (hasWarning) AppColors.CRIMSON else AppColors.GREEN
                fieldsValid[field] = inputText
                warning.text = warningText
                DrawableCompat.setTint(input.background, warnColor.raw)
                handleSubmitBtn()
            }
        }
    }

    private fun handleSubmitBtn() {
        val isValid = verifier.checkAll()
        submitBtn.isEnabled = isValid
        val btnColor = if (isValid) AppColors.GREEN else AppColors.BLUE
        val btnBackground = if (isValid) R.drawable.sign_submit_btn_active
            else R.drawable.sign_submit_btn_inactive
        submitBtn.setTextColor(btnColor.raw)
        submitBtn.setBackgroundResource(btnBackground)
    }

    protected abstract suspend fun handleSubmit(fn: (String) -> Unit)
}