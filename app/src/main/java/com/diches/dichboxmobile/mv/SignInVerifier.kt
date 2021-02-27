package com.diches.dichboxmobile.mv

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class SignInVerifier(submitBtn: Button) : SignVerifier(
        submitBtn,
        fieldsValid = mutableMapOf(SignFields.EMAIL to "", SignFields.PASSWD to ""),
        verifiers = mutableMapOf(
                SignFields.EMAIL to Regex("""([a-z_\d\.-]+)@([a-z\d]+)\.([a-z]{2,8})(\.[a-z]{2,8})*"""),
                SignFields.PASSWD to Regex("""^[\S]{5,20}""")
        )
) {
    private lateinit var passwordFields: Pair<EditText, TextView>

    open fun checkEmail(email: EditText, warning: TextView): SignInVerifier {
        handleInput(SignFields.EMAIL, email, warning) {
            when {
                !checkFieldTemplate(SignFields.EMAIL, it) -> SignWarnings.EMAIL_INVALID.text
                !verifyField(SignFields.EMAIL.getVal(), it) -> SignWarnings.EMAIL_NOT_FOUND.text
                else -> SignWarnings.EMPTY_WARNING.text
            }
        }
        return this
    }

    open fun checkPassword(password: EditText, warning: TextView): SignInVerifier {
        passwordFields = Pair(password, warning)
        handleInput(SignFields.PASSWD, password, warning) {
            if (checkFieldTemplate(SignFields.PASSWD, it))
                SignWarnings.EMPTY_WARNING.text
            else
                SignWarnings.PASSWD_INVALID.text
        }
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

        println("STATUS: $status\tBODY: $nameContainer")
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

