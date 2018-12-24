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
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by Allan Wang on 2017-06-23.
 *
 * Misc test code
 */
class UtilsTest {

    @Test
    fun colorToHex() {
        assertEquals("#ffffff", Color.WHITE.toHexString(withAlpha = false, withHexPrefix = true).toLowerCase())
    }

    @Test
    fun rounding() {
        assertEquals("1.23", 1.23456f.round(2))
        assertEquals("22.466", 22.465920439.round(3))
        assertEquals("22", 22f.round(3))
    }
}
