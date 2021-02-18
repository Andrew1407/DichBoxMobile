package com.diches.dichboxmobile.mv

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpVerifier(
        private val username: EditText,
        email: EditText,
        password: EditText,
) : SignInVerifier(email, password){
    init {
        checkUsername()
    }

    private fun checkUsername() {
        username.addTextChangedListener {
            CoroutineScope(Dispatchers.IO).launch {
                nameInputListener()
            }
        }
    }

    private suspend fun nameInputListener() {
        if (username.text.isEmpty()) return
        val input: String = username.text.toString()
        val res = verifyField("name", input)
    }
}