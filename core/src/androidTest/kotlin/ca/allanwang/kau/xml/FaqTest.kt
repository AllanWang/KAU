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
package ca.allanwang.kau.xml

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import ca.allanwang.kau.test.R
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** Created by Allan Wang on 2017-08-01. */
@RunWith(AndroidJUnit4::class)
@MediumTest
class FaqTest {

  val context: Context
    get() = ApplicationProvider.getApplicationContext<Context>()

  @Test
  fun simpleTest() {
    val data = context.kauParseFaq(R.xml.test_faq, false)
    assertEquals(2, data.size, "FAQ size is incorrect")
    assertEquals(
      "This is a question",
      data.first().question.toString(),
      "First question does not match"
    )
    assertEquals("This is an answer", data.first().answer.toString(), "First answer does not match")
    assertEquals(
      "This is another question",
      data.last().question.toString(),
      "Second question does not match"
    )
    assertEquals(
      "This is another answer",
      data.last().answer.toString(),
      "Second answer does not match"
    )
  }
}
