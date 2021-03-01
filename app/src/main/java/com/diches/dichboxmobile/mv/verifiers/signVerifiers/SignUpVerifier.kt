package com.diches.dichboxmobile.mv.verifiers.signVerifiers

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignUpVerifier(submitBtn: Button) : SignInVerifier(submitBtn) {
    init {
        templates[SignFields.NAME] = """[^\s/]{1,40}"""
        fieldsValid[SignFields.NAME] = ""

        verifier.clean().addVerifier(
                key = SignFields.NAME,
                templateWarning = SignWarnings.NAME_INVALID.text,
                templateTest = { checkFieldTemplate(SignFields.NAME, it) },
                fetchWarning = SignWarnings.NAME_TAKEN.text,
                fetchHandler = { !verifyField(SignFields.NAME.getVal(), it) }
        )

        verifier.addVerifier(
                key = SignFields.EMAIL,
                templateWarning = SignWarnings.EMAIL_INVALID.text,
                templateTest = { checkFieldTemplate(SignFields.EMAIL, it) },
                fetchWarning = SignWarnings.EMAIL_TAKEN.text,
                fetchHandler = { !verifyField(SignFields.EMAIL.getVal(), it) }
        )

        verifier.addVerifier(
                key = SignFields.PASSWD,
                templateWarning = SignWarnings.PASSWD_INVALID.text,
                templateTest = { checkFieldTemplate(SignFields.PASSWD, it) }
        )

    }

    override fun checkEmail(email: EditText, warning: TextView): SignUpVerifier {
        handleInput(SignFields.EMAIL, email, warning)
        return this
    }

    override fun checkPassword(password: EditText, warning: TextView): SignUpVerifier {
        return super.checkPassword(password, warning) as SignUpVerifier
    }

    fun checkUsername(name: EditText, warning: TextView): SignUpVerifier {
        handleInput(SignFields.NAME, name, warning)
        return this
    }

    override suspend fun handleSubmit(saveUser: (String) -> Unit) {
        val submitContainer = UserContainer.SignUp(
                fieldsValid[SignFields.NAME]!!,
                fieldsValid[SignFields.EMAIL]!!,
                fieldsValid[SignFields.PASSWD]!!
        )

        val (status, nameContainer) = withContext(Dispatchers.IO) {
            api.createUser(submitContainer)
        }
        val (name) = nameContainer as UserContainer.NameContainer

        if (status == 201 && name != null) saveUser(name)

    }
}