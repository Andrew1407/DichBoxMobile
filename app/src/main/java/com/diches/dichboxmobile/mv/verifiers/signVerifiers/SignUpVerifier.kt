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
        verifier.addInputEntry(SignFields.NAME)

        inputHandler.clean()
                .addVerifier(
                        key = SignFields.NAME,
                        templateWarning = SignWarnings.NAME_INVALID.text,
                        templateTest = { checkFieldTemplate(SignFields.NAME, it) },
                        fetchWarning = SignWarnings.NAME_TAKEN.text,
                        fetchHandler = { !verifyField(SignFields.NAME.getVal(), it) }
                )
                .addVerifier(
                        key = SignFields.EMAIL,
                        templateWarning = SignWarnings.EMAIL_INVALID.text,
                        templateTest = { checkFieldTemplate(SignFields.EMAIL, it) },
                        fetchWarning = SignWarnings.EMAIL_TAKEN.text,
                        fetchHandler = { !verifyField(SignFields.EMAIL.getVal(), it) }
                )
                .addVerifier(
                        key = SignFields.PASSWD,
                        templateWarning = SignWarnings.PASSWD_INVALID.text,
                        templateTest = { checkFieldTemplate(SignFields.PASSWD, it) }
                )

    }

    override fun checkEmail(email: EditText, warning: TextView): SignUpVerifier {
        verifier.onInputCheck(SignFields.EMAIL, email, warning)
        return this
    }

    override fun checkPassword(password: EditText, warning: TextView): SignUpVerifier {
        verifier.onInputCheck(SignFields.PASSWD, password, warning)
        return this
    }

    fun checkUsername(name: EditText, warning: TextView): SignUpVerifier {
        verifier.onInputCheck(SignFields.NAME, name, warning)
        return this
    }

    override suspend fun handleSubmit(saveUser: (String) -> Unit) {
        val values = verifier.getInput()
        val submitContainer = UserContainer.SignUp(
                name = values[SignFields.NAME]!!,
                email = values[SignFields.EMAIL]!!,
                passwd = values[SignFields.PASSWD]!!
        )

        val (status, nameContainer) = withContext(Dispatchers.IO) {
            api.createUser(submitContainer)
        }

        val (name) = nameContainer as UserContainer.NameContainer

        if (status == 201 && name != null) saveUser(name)
    }
}
