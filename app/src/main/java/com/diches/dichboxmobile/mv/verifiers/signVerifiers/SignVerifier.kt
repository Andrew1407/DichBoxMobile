package com.diches.dichboxmobile.mv.verifiers.signVerifiers

import com.diches.dichboxmobile.api.users.UserAPI
import com.diches.dichboxmobile.mv.verifiers.FieldsTemplates
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.verifiers.FieldsVerifier

abstract class SignVerifier {
    protected val api = UserAPI()
    private lateinit var saveHandler: (String) -> Unit
    protected abstract val verifier: FieldsVerifier<SignFields>
    protected abstract val templates: MutableMap<SignFields, FieldsTemplates>

    fun setSaveHandler(fn: (String) -> Unit) {
        saveHandler = fn
    }

    protected fun initVerifierClb() {
        verifier.addSubmitListener {
            handleSubmit(saveHandler)
        }
    }

    protected suspend fun verifyField(
            field: String,
            value: String
    ): Boolean {
        val container = UserContainer.VerifyField(field, value)
        val (_, res) = api.verifyField(container)
        return (res as UserContainer.VerifyFieldRes).foundValue != null
    }

    protected fun checkFieldTemplate(
            field: SignFields,
            input: String
    ): Boolean = templates[field]!!.test(input)

    protected abstract suspend fun handleSubmit(fn: (String) -> Unit)
}
