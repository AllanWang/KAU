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
package ca.allanwang.kau.kpref

import android.annotation.SuppressLint
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

/**
 * Created by Allan Wang on 2017-08-01.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class KPrefTest {

    lateinit var androidPref: TestPref
    lateinit var memPref: TestPref

    class TestPref(builder: KPrefBuilder) : KPref(builder) {

        init {
            initialize(ApplicationProvider.getApplicationContext<Context>(), "kpref_test_${System.currentTimeMillis()}")
        }

        var postSetterCount: Int = 0

        var one by kpref("one", 1)

        var two by kpref("two", 2f)

        var `true` by kpref("true", true, postSetter = {
            postSetterCount++
        })

        var hello by kpref("hello", "hello")

        var set by kpref("set", setOf("po", "ta", "to"))

        val oneShot by kprefSingle("asdf")
    }

    @Before
    fun init() {
        androidPref = TestPref(KPrefBuilderAndroid)
        androidPref.sp.edit().clear().commit()
        memPref = TestPref(KPrefBuilderInMemory)
    }

    private fun pref(action: TestPref.() -> Unit) {
        androidPref.action()
        memPref.action()
    }

    private fun <T> assertPrefEquals(expected: T, actual: TestPref.() -> T, message: String? = null) {
        assertEquals(expected, androidPref.actual(), "Android KPrefs: $message")
        assertEquals(expected, memPref.actual(), "In Mem KPrefs: $message")
    }

    @Test
    fun getDefaults() {
        assertPrefEquals(1, { one })
        assertPrefEquals(2f, { two })
        assertPrefEquals(true, { `true` })
        assertPrefEquals("hello", { hello })
        assertPrefEquals(3, { set.size })
        assertPrefEquals(setOf("po", "ta", "to"), { set })
        assertEquals(0, androidPref.sp.all.size, "Defaults should not be set automatically")
    }

    @Test
    fun setter() {
        assertPrefEquals(1, { one })
        pref { one = 2 }
        assertPrefEquals(2, { one })
        pref { hello = "goodbye" }
        assertPrefEquals("goodbye", { hello })
        assertEquals(androidPref.hello, androidPref.sp.getString("hello", "badfallback"))
        assertEquals(2, androidPref.sp.all.size)
    }

    @SuppressLint("CommitPrefEdits")
    @Test
    fun reset() {
        pref { one = 2 }
        assertPrefEquals(2, { one })
        assertPrefEquals(6, { prefMap.size }, "Prefmap does not have all elements")
        pref { reset() } // only invalidates our lazy delegate; doesn't change the actual pref
        assertPrefEquals(2, { one }, "Kpref did not properly fetch from shared prefs")
        // Android pref only
        androidPref.sp.edit().putInt("one", -1).commit()
        assertEquals(2, androidPref.one, "Lazy kpref should still retain old value")
        androidPref.reset()
        assertEquals(-1, androidPref.one, "Kpref did not refetch from shared prefs upon reset")
    }

    @Test
    fun single() {
        assertPrefEquals(true, { oneShot })
        assertPrefEquals(false, { androidPref.oneShot })
    }

    @Test
    fun postSetter() {
        pref { `true` = true }
        assertPrefEquals(1, { postSetterCount }, "Post setter was not called")
    }
}
