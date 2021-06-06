package com.diches.dichboxmobile

import com.diches.dichboxmobile.mv.verifiers.editVerifiers.MutableInputVerifier
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class MutableInputVerifierTest {
    private val verifier = MutableInputVerifier<String>()

    @Test
    fun verifiesOmittingCorrect() {
        val input1 = "input1"
        val input2 = "input2"
        val errorMessage = "invalid omitting"
        val correct = true

        verifier
            .addVerifier(
                key = "test1",
                templateWarning = "test 1 warning 1",
                templateTest = { it in input1 },
                fetchWarning = "test 1 warning 2",
                fetchHandler = { it == input1 }
            )
            .addVerifier(
                key = "test2",
                templateWarning = "test 2 warning 1",
                templateTest = { it == input2 },
            )

        runBlocking { verifier.verify("test1", input1) }
        runBlocking { verifier.verify("test2", input1) }
        assertEquals(errorMessage, !correct, verifier.checkAll())

        verifier.addOmitted("test2")
        assertEquals(errorMessage, correct, verifier.checkAll())

        val omitted = runBlocking { verifier.verify("test2", input2) }
        assertEquals(errorMessage, "", omitted)

        verifier.removeOmitted("test1")
        assertEquals(errorMessage, correct, verifier.checkAll())

        verifier.removeOmitted("test2")
        assertEquals(errorMessage, !correct, verifier.checkAll())
    }
}