package com.diches.dichboxmobile.mv

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class SignInVerifier(
        private val email: EditText,
        private val password: EditText
) : SignVerifier() {

    init {
        checkEmail()
        checkPassword()
    }

    private fun checkEmail() {
        email.addTextChangedListener {
            CoroutineScope(Dispatchers.IO).launch {
                emailInputListener()
            }
        }
    }

    private fun checkPassword() {
        password.addTextChangedListener {
            CoroutineScope(Dispatchers.IO).launch {
                passwordInputListener()
            }
        }
    }

    private suspend fun passwordInputListener() {
        val test = Regex("""[\S]{5,20}${'$'}""")
        val str = password.text.toString()
        println(test.matches(str))
    }

    private suspend fun emailInputListener() {
        if (email.text.isEmpty()) return
        val input: String = email.text.toString()
        val res = verifyField("email", input)
    }
}