package com.diches.dichboxmobile.mv.verifiers.editVerifiers.user

enum class UserEditFields {
    NAME, EMAIL, DESCRIPTION, PASSWD, NEW_PASSWD;

    fun getVal(): String = this.name.toLowerCase()
}