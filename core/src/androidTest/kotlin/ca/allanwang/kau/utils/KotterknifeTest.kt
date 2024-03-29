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

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Created by Allan Wang on 2017-07-30. */
@RunWith(AndroidJUnit4::class)
@MediumTest
class KotterknifeTest {

  lateinit var context: Context

  @Before
  fun init() {
    context = InstrumentationRegistry.getInstrumentation().context
  }

  @Test
  fun testCast() {
    class Example(context: Context) : FrameLayout(context) {
      val name: TextView by bindView(1)
    }

    val example = Example(context)
    example.addView(textViewWithId(1))
    assertNotNull(example.name)
  }

  @Test
  fun testFindCached() {
    class Example(context: Context) : FrameLayout(context) {
      val name: View by bindView(1)
    }

    val example = Example(context)
    example.addView(viewWithId(1))
    assertNotNull(example.name)
    example.removeAllViews()
    assertNotNull(example.name)
  }

  @Test
  fun testOptional() {
    class Example(context: Context) : FrameLayout(context) {
      val present: View? by bindOptionalView(1)
      val missing: View? by bindOptionalView(2)
    }

    val example = Example(context)
    example.addView(viewWithId(1))
    assertNotNull(example.present)
    assertNull(example.missing)
  }

  @Test
  fun testOptionalCached() {
    class Example(context: Context) : FrameLayout(context) {
      val present: View? by bindOptionalView(1)
      val missing: View? by bindOptionalView(2)
    }

    val example = Example(context)
    example.addView(viewWithId(1))
    assertNotNull(example.present)
    assertNull(example.missing)
    example.removeAllViews()
    example.addView(viewWithId(2))
    assertNotNull(example.present)
    assertNull(example.missing)
  }

  @Test
  fun testMissingFails() {
    class Example(context: Context) : FrameLayout(context) {
      val name: TextView? by bindView(1)
    }

    val example = Example(context)
    try {
      example.name
    } catch (e: IllegalStateException) {
      assertEquals("View ID 1 for 'name' not found.", e.message)
    }
  }

  @Test
  fun testList() {
    class Example(context: Context) : FrameLayout(context) {
      val name: List<TextView> by bindViews(1, 2, 3)
    }

    val example = Example(context)
    example.addView(viewWithId(1))
    example.addView(viewWithId(2))
    example.addView(viewWithId(3))
    assertNotNull(example.name)
    assertEquals(3, example.name.size)
  }

  @Test
  fun testListCaches() {
    class Example(context: Context) : FrameLayout(context) {
      val name: List<TextView> by bindViews(1, 2, 3)
    }

    val example = Example(context)
    example.addView(viewWithId(1))
    example.addView(viewWithId(2))
    example.addView(viewWithId(3))
    assertNotNull(example.name)
    assertEquals(3, example.name.size)
    example.removeAllViews()
    assertNotNull(example.name)
    assertEquals(3, example.name.size)
  }

  @Test
  fun testListMissingFails() {
    class Example(context: Context) : FrameLayout(context) {
      val name: List<TextView> by bindViews(1, 2, 3)
    }

    val example = Example(context)
    example.addView(viewWithId(1))
    example.addView(viewWithId(3))
    try {
      example.name
    } catch (e: IllegalStateException) {
      assertEquals("View ID 2 for 'name' not found.", e.message)
    }
  }

  @Test
  fun testOptionalList() {
    class Example(context: Context) : FrameLayout(context) {
      val name: List<TextView> by bindOptionalViews(1, 2, 3)
    }

    val example = Example(context)
    example.addView(viewWithId(1))
    example.addView(viewWithId(3))
    assertNotNull(example.name)
    assertEquals(2, example.name.size)
  }

  @Test
  fun testOptionalListCaches() {
    class Example(context: Context) : FrameLayout(context) {
      val name: List<TextView> by bindOptionalViews(1, 2, 3)
    }

    val example = Example(context)
    example.addView(viewWithId(1))
    example.addView(viewWithId(3))
    assertNotNull(example.name)
    assertEquals(2, example.name.size)
    example.removeAllViews()
    assertNotNull(example.name)
    assertEquals(2, example.name.size)
  }

  @Test
  fun testReset() {
    class Example(context: Context) : FrameLayout(context) {
      val name: View? by bindOptionalViewResettable(1)
    }

    val example = Example(context)
    example.addView(viewWithId(1))
    assertNotNull(example.name)
    example.removeAllViews()
    Kotterknife.reset(example)
    assertNull(example.name)
  }

  private fun viewWithId(id: Int): View {
    val view = View(context)
    view.id = id
    return view
  }

  private fun textViewWithId(id: Int): View {
    val view = TextView(context)
    view.id = id
    return view
  }
}
