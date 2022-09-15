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
package ca.allanwang.kau.kotlin

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import org.junit.Before
import org.junit.Test

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
