package ca.allanwang.kau.kpref

import android.annotation.SuppressLint
import androidx.test.InstrumentationRegistry
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
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
class KPrefTest {

    lateinit var pref: TestPref

    class TestPref : KPref() {

        init {
            initialize(InstrumentationRegistry.getTargetContext(), "kpref_test_${System.currentTimeMillis()}")
        }

        var one by kpref("one", 1)

        var two by kpref("two", 2f)

        var `true` by kpref("true", true)

        var hello by kpref("hello", "hello")

        var set by kpref("set", setOf("po", "ta", "to"))

        val oneShot by kprefSingle("asdf")
    }

    @Before
    fun init() {
        pref = TestPref()
        pref.sp.edit().clear().commit()
    }

    @Test
    fun getDefaults() {
        assertEquals(1, pref.one)
        assertEquals(2f, pref.two)
        assertEquals(true, pref.`true`)
        assertEquals("hello", pref.hello)
        assertEquals(3, pref.set.size)
        assertTrue(pref.set.contains("po"))
        assertTrue(pref.set.contains("ta"))
        assertTrue(pref.set.contains("to"))
        assertEquals(0, pref.sp.all.size, "Defaults should not be set automatically")
    }

    @Test
    fun setter() {
        assertEquals(1, pref.one)
        pref.one = 2
        assertEquals(2, pref.one)
        pref.hello = "goodbye"
        assertEquals("goodbye", pref.hello)
        assertEquals(pref.hello, pref.sp.getString("hello", "hello"))
        assertEquals(2, pref.sp.all.size)
    }

    @SuppressLint("CommitPrefEdits")
    @Test
    fun reset() {
        pref.one = 2
        assertEquals(2, pref.one)
        assertEquals(6, pref.prefMap.size, "Prefmap does not have all elements")
        pref.reset() //only invalidates our lazy delegate; doesn't change the actual pref
        assertEquals(2, pref.one, "Kpref did not properly fetch from shared prefs")
        pref.sp.edit().putInt("one", -1).commit()
        assertEquals(2, pref.one, "Lazy kpref should still retain old value")
        pref.reset()
        assertEquals(-1, pref.one, "Kpref did not refetch from shared prefs upon reset")
    }


    @Test
    fun single() {
        assertTrue(pref.oneShot)
        assertFalse(pref.oneShot)
    }

}