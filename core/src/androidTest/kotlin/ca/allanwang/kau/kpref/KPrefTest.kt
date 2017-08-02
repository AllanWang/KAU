package ca.allanwang.kau.kpref

import android.annotation.SuppressLint
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Allan Wang on 2017-08-01.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class ChangelogTest {

    lateinit var pref: TestPref

    class TestPref : KPref() {
        init {
            initialize(InstrumentationRegistry.getTargetContext(), "kpref_test_${System.currentTimeMillis()}")
        }

        var one: Int by kpref("one", 1)

        var `true`: Boolean by kpref("true", true)

        var hello: String by kpref("hello", "hello")

        var set: StringSet by kpref("set", setOf("po", "ta", "to"))

        val oneShot: Boolean by kprefSingle("asdf")
    }

    @Before
    fun init() {
        pref = TestPref()
    }

    @Test
    fun getDefaults() {
        assertEquals(1, pref.one)
        assertEquals(true, pref.`true`)
        assertEquals("hello", pref.hello)
        assertEquals(3, pref.set.size)
        assertTrue(pref.set.contains("po"))
        assertTrue(pref.set.contains("ta"))
        assertTrue(pref.set.contains("to"))
    }

    @Test
    fun setter() {
        assertEquals(1, pref.one)
        pref.one = 2
        assertEquals(2, pref.one)
        pref.hello = "goodbye"
        assertEquals("goodbye", pref.hello)
        assertEquals(pref.hello, pref.sp.getString("hello", "hello"))
    }

    @SuppressLint("CommitPrefEdits")
    @Test
    fun reset() {
        pref.one = 2
        assertEquals(2, pref.one)
        pref.reset() //only invalidates our lazy delegate; doesn't change the actual pref
        assertEquals(2, pref.one)
        pref.sp.edit().putInt("one", -1).commit()
        assertEquals(2, pref.one) //our lazy delegate still retains the old value
        pref.reset()
        assertEquals(-1, pref.one) //back in sync with sp
    }


    @Test
    fun single() {
        assertTrue(pref.oneShot)
        assertFalse(pref.oneShot)
    }

}