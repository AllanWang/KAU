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
import kotlin.test.assertEquals
import org.junit.Test

/**
 * Created by Allan Wang on 2017-06-23.
 *
 * Misc test code
 */
class UtilsTest {

  @Test
  fun colorToHex() {
    assertEquals(
        "#ffffff", Color.WHITE.toHexString(withAlpha = false, withHexPrefix = true).toLowerCase())
  }

  @Test
  fun colorWithAlpha() {
    val origColor = 0xFF123456.toInt()
    assertEquals(0x00123456, origColor.withAlpha(0), "Failed to convert with alpha 0")
    assertEquals(0x50123456, origColor.withAlpha(80), "Failed to convert with alpha 80")
    assertEquals(0xFF123456.toInt(), origColor.withAlpha(255), "Failed to convert with alpha 255")
    assertEquals(0xFF123456.toInt(), origColor.withAlpha(0xFF), "Failed to convert with alpha 0xFF")
    assertEquals(
        Color.TRANSPARENT, Color.BLACK.withAlpha(0), "Failed to convert black to transparent")
  }

  @Test
  fun colorWithMinAlpha() {
    val origColor = 0x80123456.toInt()
    assertEquals(origColor, origColor.withMinAlpha(0), "Failed to convert with min alpha 0")
    assertEquals(
        0xFA123456.toInt(), origColor.withMinAlpha(0xFA), "Failed to convert with min alpha 0xFA")
    assertEquals(
        Color.BLUE, Color.BLUE.withMinAlpha(89), "Failed to convert blue with min alpha 89")
  }

  @Test
  fun rounding() {
    assertEquals("1.23", 1.23456f.round(2))
    assertEquals("22.466", 22.465920439.round(3))
    assertEquals("22", 22f.round(3))
  }
}
