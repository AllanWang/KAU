/*
 * Copyright 2019 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.kotlin

import kotlin.test.assertEquals
import kotlin.test.fail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test

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
