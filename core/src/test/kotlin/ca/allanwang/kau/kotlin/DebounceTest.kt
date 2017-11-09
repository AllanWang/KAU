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
        val debounce = debounce(20) { i++ }
        assertEquals(0, i, "i should start as 0")
        (1..5).forEach { debounce() }
        Thread.sleep(50)
        assertEquals(1, i, "Debouncing did not cancel previous requests")
    }

    @Test
    fun basicExtension() {
        var i = 0
        val increment: () -> Unit = { i++ }
        (1..5).forEach { increment() }
        assertEquals(5, i, "i should be 5")
        val debounce = increment.debounce(50)
        (6..10).forEach { debounce() }
        assertEquals(5, i, "i should not have changed")
        Thread.sleep(100)
        assertEquals(6, i, "i should increment to 6")
    }

    @Test
    fun multipleDebounces() {
        var i = 0
        val debounce = debounce<Int>(20) { i += it }
        debounce(1) //ignore -> i = 0
        Thread.sleep(10)
        assertEquals(0, i)
        debounce(2) //accept -> i = 2
        Thread.sleep(30)
        assertEquals(2, i)
        debounce(4) //ignore -> i = 2
        Thread.sleep(10)
        assertEquals(2, i)
        debounce(8) //accept -> i = 10
        Thread.sleep(30)
        assertEquals(10, i)
    }

}