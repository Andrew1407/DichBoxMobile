package com.diches.dichboxmobile.mv.verifiers.signVerifiers

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.diches.dichboxmobile.api.Statuses
import com.diches.dichboxmobile.mv.verifiers.FieldsTemplates
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.verifiers.FieldsWarnings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignUpVerifier(submitBtn: Button) : SignInVerifier(submitBtn) {
    init {
        templates[SignFields.NAME] = FieldsTemplates.NAME
        verifier.addInputEntry(SignFields.NAME)

        inputHandler.clean()
            .addVerifier(
                key = SignFields.NAME,
                templateWarning = FieldsWarnings.NAME_INVALID.text,
                templateTest = { checkFieldTemplate(SignFields.NAME, it) },
                fetchWarning = FieldsWarnings.NAME_TAKEN.text,
                fetchHandler = { !verifyField(SignFields.NAME.getVal(), it) }
            )
            .addVerifier(
                key = SignFields.EMAIL,
                templateWarning = FieldsWarnings.EMAIL_INVALID.text,
                templateTest = { checkFieldTemplate(SignFields.EMAIL, it) },
                fetchWarning = FieldsWarnings.EMAIL_TAKEN.text,
                fetchHandler = { !verifyField(SignFields.EMAIL.getVal(), it) }
            )
            .addVerifier(
                key = SignFields.PASSWD,
                templateWarning = FieldsWarnings.PASSWD_INVALID.text,
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

    override suspend fun handleSubmit(saveUser: (String, String) -> Unit) {
        val values = verifier.getInput()
        val submitContainer = UserContainer.SignUp(
            name = values[SignFields.NAME]!!,
            email = values[SignFields.EMAIL]!!,
            passwd = values[SignFields.PASSWD]!!
        )

        val (st, res) = withContext(Dispatchers.IO) { api.createUser(submitContainer) }
        val (name, user_uid) = res as UserContainer.SignedContainer
        if (Statuses.CREATED.eq(st) && name != null) saveUser(name, user_uid!!)
    }
}
