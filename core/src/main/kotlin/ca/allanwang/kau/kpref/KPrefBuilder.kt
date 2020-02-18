/*
 * Copyright 2019 Allan Wang
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

interface KPrefBuilder {
    fun KPref.kpref(
        key: String,
        fallback: Boolean,
        postSetter: (value: Boolean) -> Unit = {}
    ): KPrefDelegate<Boolean>

    fun KPref.kpref(
        key: String,
        fallback: Float,
        postSetter: (value: Float) -> Unit = {}
    ): KPrefDelegate<Float>

    @Deprecated(
        "Double is not supported in SharedPreferences; cast to float yourself",
        ReplaceWith("kpref(key, fallback.toFloat(), postSetter)"),
        DeprecationLevel.WARNING
    )
    fun KPref.kpref(
        key: String,
        fallback: Double,
        postSetter: (value: Float) -> Unit = {}
    ): KPrefDelegate<Float> =
        kpref(key, fallback.toFloat(), postSetter)

    fun KPref.kpref(
        key: String,
        fallback: Int,
        postSetter: (value: Int) -> Unit = {}
    ): KPrefDelegate<Int>

    fun KPref.kpref(
        key: String,
        fallback: Long,
        postSetter: (value: Long) -> Unit = {}
    ): KPrefDelegate<Long>

    fun KPref.kpref(
        key: String,
        fallback: Set<String>,
        postSetter: (value: Set<String>) -> Unit = {}
    ): KPrefDelegate<Set<String>>

    fun KPref.kpref(
        key: String,
        fallback: String,
        postSetter: (value: String) -> Unit = {}
    ): KPrefDelegate<String>

    fun KPref.kprefSingle(key: String): KPrefSingleDelegate

    /**
     * Remove keys from pref so they revert to the default
     */
    fun KPref.deleteKeys(keys: Array<String>)
}

class KPrefBuilderAndroid(val sp: SharedPreferences) : KPrefBuilder {

    override fun KPref.kpref(key: String, fallback: Boolean, postSetter: (value: Boolean) -> Unit) =
        KPrefDelegateAndroid(
            key,
            fallback,
            this,
            this@KPrefBuilderAndroid,
            KPrefBooleanTransaction,
            postSetter
        )

    override fun KPref.kpref(key: String, fallback: Float, postSetter: (value: Float) -> Unit) =
        KPrefDelegateAndroid(
            key,
            fallback,
            this,
            this@KPrefBuilderAndroid,
            KPrefFloatTransaction,
            postSetter
        )

    override fun KPref.kpref(key: String, fallback: Int, postSetter: (value: Int) -> Unit) =
        KPrefDelegateAndroid(
            key,
            fallback,
            this,
            this@KPrefBuilderAndroid,
            KPrefIntTransaction,
            postSetter
        )

    override fun KPref.kpref(key: String, fallback: Long, postSetter: (value: Long) -> Unit) =
        KPrefDelegateAndroid(
            key,
            fallback,
            this,
            this@KPrefBuilderAndroid,
            KPrefLongTransaction,
            postSetter
        )

    override fun KPref.kpref(
        key: String,
        fallback: Set<String>,
        postSetter: (value: Set<String>) -> Unit
    ) =
        KPrefDelegateAndroid(
            key,
            fallback,
            this,
            this@KPrefBuilderAndroid,
            KPrefSetTransaction,
            postSetter
        )

    override fun KPref.kpref(key: String, fallback: String, postSetter: (value: String) -> Unit) =
        KPrefDelegateAndroid(
            key,
            fallback,
            this,
            this@KPrefBuilderAndroid,
            KPrefStringTransaction,
            postSetter
        )

    override fun KPref.kprefSingle(key: String) =
        KPrefSingleDelegateAndroid(key, this, this@KPrefBuilderAndroid)

    override fun KPref.deleteKeys(keys: Array<String>) {
        // Remove pref listing
        sp.edit().apply {
            keys.forEach { remove(it) }
        }.apply()
        // Clear cached values
        keys.forEach { prefMap[it]?.invalidate() }
    }
}

object KPrefBuilderInMemory : KPrefBuilder {

    override fun KPref.kpref(key: String, fallback: Boolean, postSetter: (value: Boolean) -> Unit) =
        KPrefDelegateInMemory(key, fallback, this, postSetter)

    override fun KPref.kpref(key: String, fallback: Float, postSetter: (value: Float) -> Unit) =
        KPrefDelegateInMemory(key, fallback, this, postSetter)

    override fun KPref.kpref(key: String, fallback: Int, postSetter: (value: Int) -> Unit) =
        KPrefDelegateInMemory(key, fallback, this, postSetter)

    override fun KPref.kpref(key: String, fallback: Long, postSetter: (value: Long) -> Unit) =
        KPrefDelegateInMemory(key, fallback, this, postSetter)

    override fun KPref.kpref(
        key: String,
        fallback: Set<String>,
        postSetter: (value: Set<String>) -> Unit
    ) =
        KPrefDelegateInMemory(key, fallback, this, postSetter)

    override fun KPref.kpref(key: String, fallback: String, postSetter: (value: String) -> Unit) =
        KPrefDelegateInMemory(key, fallback, this, postSetter)

    override fun KPref.kprefSingle(key: String) = KPrefSingleDelegateInMemory(key, this)

    override fun KPref.deleteKeys(keys: Array<String>) {
        // Clear cached values
        keys.forEach { prefMap[it]?.invalidate() }
    }
}
