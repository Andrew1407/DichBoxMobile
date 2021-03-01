package com.diches.dichboxmobile.mv.verifiers.signVerifiers

enum class SignFields {
    EMAIL, PASSWD, NAME;

    fun getVal(): String = this.name.toLowerCase()
}