package ca.allanwang.kau.kotlin

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Tests geared towards coroutines
 */
class CoroutineTest {

    /**
     * If a job is cancelled, then a switch to a new context will not run
     */
    @Test
    fun implicitCancellationBefore() {
        val job = Job()
        var id = 0
        try {
            runBlocking(job) {
                id++
                job.cancel()
                withContext(Dispatchers.IO) {
                    fail("Context switch should not be reached")
                }
            }
        } catch (ignore: CancellationException) {
        } finally {
            assertEquals(1, id, "Launcher never executed")
        }
    }

    /**
     * If a job is cancelled, then a switch from a new context will not run
     */
    @Test
    fun implicitCancellationAfter() {
        val job = Job()
        var id = 0
        try {
            runBlocking(job) {
                withContext(Dispatchers.IO) {
                    id++
                    job.cancel()
                }
                fail("Post context switch should not be reached")
            }
        } catch (ignore: CancellationException) {
        } finally {
            assertEquals(1, id, "Context switch never executed")
        }
    }
}