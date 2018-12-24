package ca.allanwang.kau.kotlin

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Created by Allan Wang on 2017-07-29.
 *
 * Test code for [LazyResettable]
 */
class LazyResettableTest {

    lateinit var registry: LazyResettableRegistry

    @Before
    fun init() {
        registry = LazyResettableRegistry()
    }

    @Test
    fun basic() {
        val timeDelegate = lazyResettable { System.currentTimeMillis() }
        val time: Long by timeDelegate
        registry.add(timeDelegate)
        val t1 = time
        Thread.sleep(5)
        val t2 = time
        registry.invalidateAll()
        Thread.sleep(5)
        val t3 = time
        assertEquals(t1, t2, "Lazy resettable not returning same value after second call")
        assertNotEquals(t1, t3, "Lazy resettable not invalidated by registry")
    }
}