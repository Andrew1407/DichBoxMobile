package com.diches.dichboxmobile.mv.verifiers

open class InputVerifier <K> (private val defaultWarning: String = "") {
    private val verifiers: MutableMap<K, WarningHandler> = mutableMapOf()
    protected val warnings: MutableMap<K, Pair<String, Boolean>> = mutableMapOf()

    open fun addVerifier(
            key: K,
            templateWarning: String,
            templateTest: (String) -> Boolean,
            fetchWarning: String? = null,
            fetchHandler: (suspend (String) -> Boolean)? = null
    ): InputVerifier<K> {
        val verifier = WarningHandler(
                templateWarning, templateTest, fetchWarning, fetchHandler
        )
        verifiers[key] = verifier
        warnings[key] = Pair(defaultWarning, false)
        return this
    }

    open suspend fun verify(key: K, input: String): String {
        val verifier = verifiers[key]!!
        val isCorrect = true
        if (!verifier.templateTest(input)) {
             warnings[key] = Pair(verifier.templateWarning, !isCorrect)
             return  warnings[key]!!.first
        }

        if (verifier.fetchHandler != null)
            if (!verifier.fetchHandler.invoke(input)) {
                warnings[key] = Pair(verifier.fetchWarning!!, !isCorrect)
                return  warnings[key]!!.first
            }

        warnings[key] = Pair(defaultWarning, isCorrect)
        return warnings[key]!!.first
    }

    fun clean(): InputVerifier<K> {
        warnings.clear()
        verifiers.clear()
        return this
    }

    open fun checkAll(): Boolean = warnings.values
            .map { it.second }
            .reduce { acc, b -> acc && b }

    private data class WarningHandler(
            val templateWarning: String,
            val templateTest: (String) -> Boolean,
            val fetchWarning: String? = null,
            val fetchHandler: (suspend (String) -> Boolean)? = null
    )
}