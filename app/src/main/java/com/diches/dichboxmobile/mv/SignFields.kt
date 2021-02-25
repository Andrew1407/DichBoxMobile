package com.diches.dichboxmobile.mv

enum class SignFields {
    EMAIL, PASSWD, NAME;

    fun getVal(): String = this.name.toLowerCase()
}