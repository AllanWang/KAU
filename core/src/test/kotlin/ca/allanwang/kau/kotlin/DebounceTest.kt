package ca.allanwang.kau.kotlin

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by Allan Wang on 2017-08-05.
 */
class DebounceTest {

    @Test
    fun basic() {
        var i = 0
        val debounce = EmptyDebouncer(20) { i++ }
        assertEquals(0, i, "i should start as 0")
        (1..5).forEach { debounce() }
        Thread.sleep(50)
        assertEquals(1, i, "Debouncing did not cancel previous requests")
    }

    @Test
    fun basicExtension() {
        var i = 0
        val increment = { i++ }
        (1..5).forEach { increment() }
        assertEquals(5, i, "i should be 5")
        val debounce = increment.debounce(50)
        (6..10).forEach { debounce() }
        assertEquals(5, i, "i should not have changed")
        Thread.sleep(100)
        assertEquals(6, i, "i should increment to 6")
    }

}