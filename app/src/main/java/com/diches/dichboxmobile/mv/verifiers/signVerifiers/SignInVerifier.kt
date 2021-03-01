package com.diches.dichboxmobile.mv.verifiers.signVerifiers

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.verifiers.FieldsVerifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class SignInVerifier(submitBtn: Button) : SignVerifier(submitBtn) {
    private lateinit var passwordFields: Pair<EditText, TextView>
    override val verifier = FieldsVerifier<SignFields>()
    override var templates: MutableMap<SignFields, String> = mutableMapOf(
        SignFields.EMAIL to """([a-z_\d\.-]+)@([a-z\d]+)\.([a-z]{2,8})(\.[a-z]{2,8})*""",
        SignFields.PASSWD to """^[\S]{5,20}"""
    )
    override val fieldsValid: MutableMap<SignFields, String> = mutableMapOf(
            SignFields.EMAIL to "", SignFields.PASSWD to ""
    )

    init {
        verifier.addVerifier(
                key = SignFields.EMAIL,
                templateWarning = SignWarnings.EMAIL_INVALID.text,
                templateTest = { checkFieldTemplate(SignFields.EMAIL, it) },
                fetchWarning = SignWarnings.EMAIL_NOT_FOUND.text,
                fetchHandler = { verifyField(SignFields.EMAIL.getVal(), it) }
        )

        verifier.addVerifier(
                key = SignFields.PASSWD,
                templateWarning = SignWarnings.PASSWD_INVALID.text,
                templateTest = { checkFieldTemplate(SignFields.PASSWD, it) }
        )
    }

    open fun checkEmail(email: EditText, warning: TextView): SignInVerifier {
        handleInput(SignFields.EMAIL, email, warning)
        return this
    }

    open fun checkPassword(password: EditText, warning: TextView): SignInVerifier {
        passwordFields = Pair(password, warning)
        handleInput(SignFields.PASSWD, password, warning)
        return this
    }

    override suspend fun handleSubmit(saveUser: (String) -> Unit) {
        val submitContainer = UserContainer.SignIn(
                fieldsValid[SignFields.EMAIL]!!,
                fieldsValid[SignFields.PASSWD]!!
        )

        val (status, nameContainer) = withContext(Dispatchers.IO) {
            api.enterUser(submitContainer)
        }
        val (name) = nameContainer as UserContainer.NameContainer

        if (status == 400 && name == null) {
            val crimson = -0x23ebc4
            val (field, warning) = passwordFields
            DrawableCompat.setTint(field.background, crimson)
            warning.text = SignWarnings.PASSWD_INCORRECT.text
            return
        }

        if (name != null) saveUser(name)
    }

}

