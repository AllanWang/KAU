/*
 * Copyright 2018 Allan Wang
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
package ca.allanwang.kau.utils

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Allan Wang on 2018-12-24.
 *
 * Misc test code that are dependent on android
 */
@RunWith(AndroidJUnit4::class)
class UtilsAndroidTest {

    @Test
    fun colorBlend() {
        assertEquals(0x22446688, 0x11335577.blendWith(0x33557799, 0.5f), "Failed to blend with 50% ratio")
        assertEquals(0x11335577, 0x11335577.blendWith(0x33557799, 0.0f), "Failed to blend with 0% ratio")
        assertEquals(0x33557799, 0x22446688.blendWith(0x33557799, 1.0f), "Failed to blend with 100% ratio")
    }

    @Test
    fun lighten() {
        assertEquals(Color.WHITE, Color.WHITE.lighten(0.35f), "Should not be able to further lighten white")
        assertEquals(0xFFEEAAEA.toInt(), 0xFFDD55D5.toInt().lighten(0.5f), "Failed to lighten color by 50%")
    }

    @Test
    fun darken() {
        assertEquals(Color.BLACK, Color.BLACK.darken(0.35f), "Should not be able to further darken black")
        assertEquals(0xFF224424.toInt(), 0xFF448848.toInt().darken(0.5f), "Failed to darken color by 50%")
    }
}
