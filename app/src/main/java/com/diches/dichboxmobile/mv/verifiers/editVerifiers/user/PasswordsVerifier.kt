package com.diches.dichboxmobile.mv.verifiers.editVerifiers.user

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.tools.AppColors
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.verifiers.FieldsTemplates
import com.diches.dichboxmobile.mv.verifiers.FieldsWarnings
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.MutableInputVerifier
import kotlinx.coroutines.*

class PasswordsVerifier (
        private val passwdArea: LinearLayout,
        switchBts: Pair<Button, Button>,
        defaultVisible: Boolean
) {
    private val showAreaBtn = switchBts.first
    private val hideAreaBtn = switchBts.second
    private lateinit var username: String
    private lateinit var api: UserAPI
    private val inputVerifier = MutableInputVerifier<UserEditFields>()
    private lateinit var checkClb: () -> Unit
    private var passwdStr = ""
    private var newPasswdStr = ""
    private var isCorrect = false
    private var passwdCorrect = false
    private var newPasswdCorrect = false

    private lateinit var passwd: EditText
    private lateinit var newPasswd: EditText
    private lateinit var passwdWarn: TextView
    private lateinit var newPasswdWarn: TextView

    init {
        passwdArea.visibility = if (defaultVisible) View.VISIBLE else View.GONE
        showAreaBtn.visibility = if (!defaultVisible) View.VISIBLE else View.GONE

        inputVerifier
                .addVerifier(
                        key = UserEditFields.PASSWD,
                        templateWarning = FieldsWarnings.PASSWD_INVALID.text,
                        templateTest = { FieldsTemplates.PASSWD.test(it) },
                        fetchWarning = FieldsWarnings.PASSWD_INCORRECT.text,
                        fetchHandler = { verifyPassword(it) }
                ).addOmitted(UserEditFields.PASSWD)
                .addVerifier(
                        key = UserEditFields.NEW_PASSWD,
                        templateWarning = FieldsWarnings.PASSWD_INVALID.text,
                        templateTest = { FieldsTemplates.PASSWD.test(it) }
                ).addOmitted(UserEditFields.NEW_PASSWD)
    }

    fun setCheckClb(clb: () -> Unit): PasswordsVerifier {
        checkClb = clb
        return this
    }

    fun getAreaState(): Boolean = passwdArea.visibility == View.VISIBLE

    fun setVerifyParams(apiParam: UserAPI, data: String): PasswordsVerifier {
        api = apiParam
        username = data
        return this
    }

    private suspend fun verifyPassword(value: String): Boolean {
        val container = UserContainer.VerifyPasswd(username, value)
        val (_, res) = api.verifyPassword(container)
        return (res as UserContainer.VerifyPasswdRes).checked
    }

    fun addPasswdCheck(passwdInput: EditText, warning: TextView): PasswordsVerifier {
        passwd = passwdInput
        passwdWarn = warning
        passwd.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {
                val inputText = passwd.text.toString()
                val rotated = passwdStr == inputText
                if (rotated) return@launch
                inputVerifier.removeOmitted(UserEditFields.PASSWD)
                val warningText = runBlocking(Dispatchers.IO) {
                    inputVerifier.verify(UserEditFields.PASSWD, inputText)
                }
                val hasWarning = warningText.isNotEmpty()
                passwdStr = inputText
                decorateFields(warningText, passwd, warning)
                newPasswd.isEnabled = !hasWarning
                passwdCorrect = !hasWarning
                isCorrect = newPasswdCorrect && passwdCorrect
                checkClb()
            }
        }
        return this
    }

    fun addNewPasswdCheck(passwdInput: EditText, warning: TextView): PasswordsVerifier {
        newPasswd = passwdInput
        newPasswdWarn = warning
        newPasswd.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {
                val inputText = newPasswd.text.toString()
                val rotated = newPasswdStr == inputText
                if (rotated) return@launch
                inputVerifier.removeOmitted(UserEditFields.NEW_PASSWD)
                val warningText = inputVerifier.verify(UserEditFields.NEW_PASSWD, inputText)
                newPasswdStr = inputText
                decorateFields(warningText, newPasswd, warning)
                newPasswdCorrect = inputVerifier.checkAll()
                isCorrect = newPasswdCorrect && passwdCorrect
                checkClb()
            }
        }
        return this
    }

    fun getChecked(): Pair<Boolean, String> = Pair(isCorrect, newPasswdStr)

    fun handleSwitchAreaState(): PasswordsVerifier {
        hideAreaBtn.setOnClickListener {
            passwdArea.visibility = View.GONE
            inputVerifier.removeOmitted(UserEditFields.PASSWD)
            inputVerifier.removeOmitted(UserEditFields.NEW_PASSWD)
            passwd.text.clear()
            newPasswd.text.clear()
            newPasswd.isEnabled = false
            isCorrect = false
            passwdCorrect = false
            newPasswdCorrect = false
            passwdWarn.text = ""
            newPasswdWarn.text = ""
            showAreaBtn.visibility = View.VISIBLE
            passwdStr = ""
            newPasswdStr = ""
        }

        showAreaBtn.setOnClickListener {
            showAreaBtn.visibility = View.GONE
            passwdArea.visibility = View.VISIBLE
        }

        return this
    }

    private fun decorateFields(
            warnMessage: String,
            input: EditText,
            warning: TextView
    ) {
        if (!passwdArea.isVisible) {
            DrawableCompat.setTint(input.background, AppColors.BLUE.raw)
            return
        }
        val hasWarning = warnMessage.isNotEmpty()
        val warnColor = if (hasWarning) AppColors.CRIMSON else AppColors.GREEN
        warning.text = warnMessage
        DrawableCompat.setTint(input.background, warnColor.raw)
    }
}