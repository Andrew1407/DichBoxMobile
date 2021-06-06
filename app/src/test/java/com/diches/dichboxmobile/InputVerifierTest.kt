package com.diches.dichboxmobile

import com.diches.dichboxmobile.mv.verifiers.InputVerifier
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.lang.NullPointerException

class InputVerifierTest {
    private val verifier = InputVerifier<String>()

    @Test
    fun verifiesCorrect() {
        val input1 = "input1"
        val input2 = "input2"
        val errorMessage = "invalid verification"
        val correct = true
        var warningGot: String

        assertThrows("wrong null pointer behaviour", NullPointerException::class.java) {
            runBlocking { verifier.verify("test1", input1) }
        }

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

        assertEquals(errorMessage, !correct, verifier.checkAll())

        warningGot = runBlocking { verifier.verify("test1", "wrong string") }
        assertEquals(errorMessage, "test 1 warning 1", warningGot)
        assertEquals(errorMessage, !correct, verifier.checkAll())

        warningGot = runBlocking { verifier.verify("test1", input1.substring(1)) }
        assertEquals(errorMessage, "test 1 warning 2", warningGot)
        assertEquals(errorMessage, !correct, verifier.checkAll())

        warningGot = runBlocking { verifier.verify("test1", input1) }
        assertEquals(errorMessage, "", warningGot)
        assertEquals(errorMessage, !correct, verifier.checkAll())

        warningGot = runBlocking { verifier.verify("test2", input1) }
        assertEquals(errorMessage, "test 2 warning 1", warningGot)
        assertEquals(errorMessage, !correct, verifier.checkAll())

        warningGot = runBlocking { verifier.verify("test2", input2) }
        assertEquals(errorMessage, "", warningGot)
        assertEquals(errorMessage, correct, verifier.checkAll())

        runBlocking { verifier.verify("test1", "wrong string") }
        assertEquals(errorMessage, !correct, verifier.checkAll())

        runBlocking { verifier.verify("test1", input1) }
        runBlocking { verifier.verify("test2", input2) }
        verifier.clean()
        assertThrows("wrong null pointer behaviour", NullPointerException::class.java) {
            runBlocking { verifier.verify("test1", input1) }
        }
    }
}