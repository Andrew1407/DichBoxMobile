package com.diches.dichboxmobile.mv.verifiers.editVerifiers.user

import java.util.*

enum class UserEditFields {
    NAME, EMAIL, DESCRIPTION, PASSWD, NEW_PASSWD;

    fun getVal(): String = this.name.lowercase(Locale.getDefault())
}