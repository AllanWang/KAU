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
package ca.allanwang.kau.ui

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProgressAnimatorTest {

    private fun ProgressAnimator.test() {
        startActions.runAll()
        var value = 0f
        while (value < 1f) {
            apply(value)
            value += 0.05f
        }
        apply(1f)
        endActions.runAll()
    }

    @Test
    fun `basic run`() {
        var i = 0f
        ProgressAnimator.ofFloat {
            withAnimator {
                i = it
            }
        }.test()
        assertEquals(1f, i)
    }

    @Test
    fun `start end hooks`() {
        var i = 0
        ProgressAnimator.ofFloat {
            withStartAction { i = 1 }
            withDisposableAnimator { assertEquals(1, i); true }
            withEndAction {
                assertEquals(0, animatorCount, "Disposable animator not removed")
                i = 2
            }
        }.test()
        assertEquals(2, i)
    }

    @Test
    fun `disposable actions`() {
        var i = 0f
        ProgressAnimator.ofFloat {
            withDisposableAnimator {
                i = if (it < 0.5f) it else 0.5f
                i > 0.5f
            }
            withAnimator {
                assertEquals(Math.min(it, 0.5f), i)
            }
        }.test()
    }

    @Test
    fun `point action`() {
        var called = false
        var i = 0f
        ProgressAnimator.ofFloat {
            withPointAnimator(0.5f) {
                assertFalse(called)
                i = it
                called = true
            }
        }.test()
        assertTrue(called)
        assertTrue(i > 0.5f)
    }
}
