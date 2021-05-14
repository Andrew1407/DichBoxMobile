package com.diches.dichboxmobile.mv.verifiers

enum class FieldsTemplates(private val exp: String) {
    EMAIL("""([a-z_\d\.-]+)@([a-z\d]+)\.([a-z]{2,8})(\.[a-z]{2,8})*"""),
    PASSWD("""^[\S]{5,20}"""),
    NAME("""[^#%\?\s/]{1,40}""");

    fun test(str: String): Boolean = Regex(this.exp).matches(str)
}