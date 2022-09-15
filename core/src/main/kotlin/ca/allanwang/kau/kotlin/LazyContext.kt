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

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.AnimRes
import androidx.annotation.InterpolatorRes

/**
 * Created by Allan Wang on 2017-05-30.
 *
 * Lazy retrieval of context based items Items are retrieved using delegateName(context)
 */
fun lazyInterpolator(@InterpolatorRes id: Int) =
  lazyContext<Interpolator> { AnimationUtils.loadInterpolator(it, id) }

fun lazyAnimation(@AnimRes id: Int) =
  lazyContext<Animation> { AnimationUtils.loadAnimation(it, id) }

fun <T> lazyContext(initializer: (context: Context) -> T): LazyContext<T> = LazyContext(initializer)

class LazyContext<out T>(private val initializer: (context: Context) -> T, lock: Any? = null) {
  @Volatile private var _value: Any? = UNINITIALIZED
  private val lock = lock ?: this

  fun invalidate() {
    _value = UNINITIALIZED
  }

  operator fun invoke(context: Context): T {
    val _v1 = _value
    if (_v1 !== UNINITIALIZED) @Suppress("UNCHECKED_CAST") return _v1 as T

    return synchronized(lock) {
      val _v2 = _value
      if (_v2 !== UNINITIALIZED) {
        @Suppress("UNCHECKED_CAST")
        _v2 as T
      } else {
        val typedValue = initializer(context)
        _value = typedValue
        typedValue
      }
    }
  }
}
