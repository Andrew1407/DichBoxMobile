package com.diches.dichboxmobile.mv

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.diches.dichboxmobile.datatypes.UserContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignUpVerifier(submitBtn: Button) : SignInVerifier(submitBtn){
    init {
        verifiers[SignFields.NAME] = Regex("""[^\s/]{1,40}""")
        fieldsValid[SignFields.NAME] = ""
    }

    override fun checkEmail(email: EditText, warning: TextView): SignUpVerifier {
        handleInput(SignFields.EMAIL, email, warning) {
            when {
                !checkFieldTemplate(SignFields.EMAIL, it) -> SignWarnings.EMAIL_INVALID.text
                verifyField(SignFields.EMAIL.getVal(), it) -> SignWarnings.EMAIL_TAKEN.text
                else -> SignWarnings.EMPTY_WARNING.text
            }
        }
        return this
    }

    override fun checkPassword(password: EditText, warning: TextView): SignUpVerifier {
        return super.checkPassword(password, warning) as SignUpVerifier
    }

    fun checkUsername(name: EditText, warning: TextView): SignUpVerifier {
        handleInput(SignFields.NAME, name, warning) {
            when {
                !checkFieldTemplate(SignFields.NAME, it) -> SignWarnings.NAME_INVALID.text
                verifyField(SignFields.NAME.getVal(), it) -> SignWarnings.NAME_TAKEN.text
                else -> SignWarnings.EMPTY_WARNING.text
            }
        }
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

        println("STATUS: $status\tBODY: $nameContainer")
        if (status == 201 && name != null) withContext(Dispatchers.IO) {
            saveUser(name)
        }

    }
}