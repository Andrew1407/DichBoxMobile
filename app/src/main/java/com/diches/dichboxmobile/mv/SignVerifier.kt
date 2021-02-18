package com.diches.dichboxmobile.mv

import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.datatypes.UserContainer

abstract class SignVerifier {
    protected val api = UserAPI()

    protected suspend fun verifyField(
            field: String,
            value: String
    ): Boolean {
        val container = UserContainer.VerifyField(field, value)
        val (foundValue) = api.verifyField(container)
        println("VAL $foundValue")
        return foundValue != null
    }

}