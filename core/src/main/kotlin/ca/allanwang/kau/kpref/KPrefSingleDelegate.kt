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

import android.content.SharedPreferences
import ca.allanwang.kau.kotlin.ILazyResettable

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Singular KPref Delegate for booleans When the shared pref is not initialized, it will return
 * [true] then set the pref to [false] All subsequent retrievals will be [false] This is useful for
 * one time toggles such as showcasing items
 */
interface KPrefSingleDelegate : ILazyResettable<Boolean>

class KPrefSingleDelegateAndroid
internal constructor(
  private val key: String,
  private val pref: KPref,
  private val prefBuilder: KPrefBuilderAndroid
) : KPrefSingleDelegate {

  @Volatile private var _value: Boolean? = null
  private val lock = this

  private val sp: SharedPreferences
    get() = prefBuilder.sp

  init {
    if (pref.prefMap.containsKey(key))
      throw KPrefException("$key is already used elsewhere in preference ${pref.preferenceName}")
    pref.prefMap[key] = this
  }

  override fun invalidate() {
    _value = null
  }

  override val value: Boolean
    get() {
      val _v1 = _value
      if (_v1 != null) {
        return _v1
      }
      return synchronized(lock) {
        val _v2 = _value
        if (_v2 != null) {
          _v2
        } else {
          _value = sp.getBoolean(key, true)
          if (_value!!) {
            sp.edit().putBoolean(key, false).apply()
            _value = false
            true
          } else false
        }
      }
    }

  override fun isInitialized(): Boolean = _value != null

  override fun toString(): String =
    if (isInitialized()) value.toString() else "Lazy kPref $key not initialized yet."
}

class KPrefSingleDelegateInMemory
internal constructor(private val key: String, private val pref: KPref) : KPrefSingleDelegate {
  @Volatile private var _value: Boolean? = null
  private val lock = this

  init {
    if (pref.prefMap.containsKey(key))
      throw KPrefException("$key is already used elsewhere in preference ${pref.preferenceName}")
    pref.prefMap[key] = this
  }

  override fun invalidate() {
    // No op
  }

  override val value: Boolean
    get() {
      val _v1 = _value
      if (_v1 != null) {
        return _v1
      }
      return synchronized(lock) {
        val _v2 = _value
        if (_v2 != null) {
          _v2
        } else {
          _value = false
          true
        }
      }
    }

  override fun isInitialized(): Boolean = _value != null

  override fun toString(): String =
    if (isInitialized()) value.toString() else "Lazy kPref $key not initialized yet."
}
