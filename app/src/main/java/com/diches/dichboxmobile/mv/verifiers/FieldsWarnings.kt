package com.diches.dichboxmobile.mv.verifiers

enum class FieldsWarnings(val text: String) {
    EMAIL_INVALID("Incorrect email input form"),
    EMAIL_TAKEN("This email is already taken"),
    EMAIL_NOT_FOUND("This email is not registered"),

    NAME_INVALID("Username should be unique, 5-40 symbols length (no spaces, \"?\", \"/\", \"#\", \"%\")"),
    NAME_TAKEN("This username is already taken"),

    PASSWD_INVALID("Password length should be 5-16 symbols (no spaces)"),
    PASSWD_INCORRECT("Wrong password")
}