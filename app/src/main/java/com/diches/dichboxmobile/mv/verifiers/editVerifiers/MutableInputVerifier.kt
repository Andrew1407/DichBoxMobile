package com.diches.dichboxmobile.mv.verifiers.editVerifiers

import com.diches.dichboxmobile.mv.verifiers.InputVerifier

class MutableInputVerifier <K>: InputVerifier<K>() {
    private val omitList = mutableListOf<K>()

    fun addOmitted(key: K): MutableInputVerifier<K> {
        if (omitList.indexOf(key) == -1) omitList.add(key)
        return this
    }

    fun removeOmitted(key: K) {
        omitList.remove(key)
    }

    override fun addVerifier(
            key: K,
            templateWarning: String,
            templateTest: (String) -> Boolean,
            fetchWarning: String?,
            fetchHandler: (suspend (String) -> Boolean)?
    ): MutableInputVerifier<K> {
        return super.addVerifier(key, templateWarning, templateTest, fetchWarning, fetchHandler)
                as MutableInputVerifier<K>
    }

    override suspend fun verify(key: K, input: String): String {
        if(omitList.indexOf(key) != -1) return ""
        return super.verify(key, input)
    }

    override fun checkAll(): Boolean {
        val correctValues = warnings
                .filter { omitList.indexOf(it.key) == -1 }
                .map { it.value.second }
         return if (correctValues.isEmpty()) false
            else correctValues.reduce { acc, b -> acc && b }
    }
}