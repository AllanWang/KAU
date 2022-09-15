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
package ca.allanwang.kau.sample.utils

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun <T> index(index: Int, matcher: Matcher<T>): Matcher<T> =
    object : BaseMatcher<T>() {

      var current = 0

      override fun describeTo(description: Description) {
        description.appendText("Should return item at index $index")
      }

      override fun matches(item: Any?): Boolean {
        println("AA")
        return matcher.matches(item) && current++ == index
      }
    }

fun <T> first(matcher: Matcher<T>): Matcher<T> = index(0, matcher)
