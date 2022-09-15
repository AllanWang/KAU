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

import java.io.Serializable
import kotlin.reflect.KProperty

/**
 * Created by Allan Wang on 2017-05-30.
 *
 * Lazy delegate that can be invalidated if needed https://stackoverflow.com/a/37294840/4407321
 */
internal object UNINITIALIZED

fun <T : Any> lazyResettable(initializer: () -> T): LazyResettable<T> =
  LazyResettable<T>(initializer)

class LazyResettable<T : Any>(private val initializer: () -> T, lock: Any? = null) :
  ILazyResettable<T>, Serializable {
  @Volatile private var _value: Any = UNINITIALIZED
  private val lock = lock ?: this

  override fun invalidate() {
    _value = UNINITIALIZED
  }

  override val value: T
    get() {
      val _v1 = _value
      if (_v1 !== UNINITIALIZED) @Suppress("UNCHECKED_CAST") return _v1 as T

      return synchronized(lock) {
        val _v2 = _value
        if (_v2 !== UNINITIALIZED) {
          @Suppress("UNCHECKED_CAST")
          _v2 as T
        } else {
          val typedValue = initializer()
          _value = typedValue
          typedValue
        }
      }
    }

  override fun isInitialized(): Boolean = _value !== UNINITIALIZED

  override fun toString(): String =
    if (isInitialized()) value.toString() else "Lazy value not initialized yet."

  operator fun setValue(any: Any, property: KProperty<*>, t: T) {
    _value = t
  }
}

interface ILazyResettable<out T> : Lazy<T> {
  fun invalidate()
}

interface ILazyResettableRegistry {
  fun <T : Any> lazy(initializer: () -> T): LazyResettable<T>
  fun <T : Any> add(resettable: LazyResettable<T>): LazyResettable<T>
  fun invalidateAll()
  fun clear()
}

/**
 * The following below is a helper class that registers all resettables into a weakly held list All
 * resettables can therefore be invalidated at once
 */
class LazyResettableRegistry : ILazyResettableRegistry {

  var lazyRegistry: MutableList<LazyResettable<*>> = mutableListOf()

  override fun <T : Any> lazy(initializer: () -> T): LazyResettable<T> =
    add(lazyResettable(initializer))

  override fun <T : Any> add(resettable: LazyResettable<T>): LazyResettable<T> {
    lazyRegistry.add(resettable)
    return resettable
  }

  override fun invalidateAll() = lazyRegistry.forEach { it.invalidate() }

  override fun clear() = lazyRegistry.clear()
}
