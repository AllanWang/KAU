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
 * Implementation of a kpref data item
 * Contains a unique key for the shared preference as well as a nonnull fallback item
 * Also contains an optional mutable postSetter that will be called every time a new value is given
 */

interface KPrefDelegate<T> : ILazyResettable<T> {
    operator fun setValue(any: Any, property: kotlin.reflect.KProperty<*>, t: T)
}

class KPrefException(message: String) : IllegalAccessException(message)

class KPrefDelegateAndroid<T> internal constructor(
    private val key: String,
    private val fallback: T,
    private val pref: KPref,
    private val prefBuilder: KPrefBuilderAndroid,
    private val transaction: KPrefTransaction<T>,
    private var postSetter: (value: T) -> Unit = {}
) : KPrefDelegate<T> {

    private object UNINITIALIZED

    private val sp: SharedPreferences get() = prefBuilder.sp

    @Volatile
    private var _value: Any? = UNINITIALIZED
    private val lock = this

    init {
        if (pref.prefMap.containsKey(key))
            throw KPrefException("$key is already used elsewhere in preference ${pref.preferenceName}")
        pref.prefMap[key] = this@KPrefDelegateAndroid
    }

    override fun invalidate() {
        _value = UNINITIALIZED
    }

    @Suppress("UNCHECKED_CAST")
    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED)
                return _v1 as T

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED) {
                    _v2 as T
                } else {
                    _value = transaction.get(sp, key, fallback)
                    _value as T
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy kPref $key not initialized yet."

    override operator fun setValue(any: Any, property: kotlin.reflect.KProperty<*>, t: T) {
        _value = t
        val editor = sp.edit()
        transaction.set(editor, key, t)
        editor.apply()
        postSetter(t)
    }
}

class KPrefDelegateInMemory<T> internal constructor(
    private val key: String,
    private val fallback: T,
    private val pref: KPref,
    private var postSetter: (value: T) -> Unit = {}
) : KPrefDelegate<T> {

    private object UNINITIALIZED

    @Volatile
    private var _value: Any? = UNINITIALIZED
    private val lock = this

    init {
        if (pref.prefMap.containsKey(key))
            throw KPrefException("$key is already used elsewhere in preference ${pref.preferenceName}")
        pref.prefMap[key] = this
    }

    override fun invalidate() {
        // No op
    }

    @Suppress("UNCHECKED_CAST")
    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED)
                return _v1 as T

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED) {
                    _v2 as T
                } else {
                    _value = fallback
                    _value as T
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy kPref $key not initialized yet."

    override operator fun setValue(any: Any, property: kotlin.reflect.KProperty<*>, t: T) {
        _value = t
        postSetter(t)
    }
}
