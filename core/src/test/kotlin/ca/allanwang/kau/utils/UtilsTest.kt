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