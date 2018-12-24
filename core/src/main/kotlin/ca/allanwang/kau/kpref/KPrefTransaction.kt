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

internal interface KPrefTransaction<T> {
    fun get(prefs: SharedPreferences, key: String, fallback: T): T
    fun set(editor: SharedPreferences.Editor, key: String, data: T)
}

internal object KPrefBooleanTransaction : KPrefTransaction<Boolean> {
    override fun get(prefs: SharedPreferences, key: String, fallback: Boolean) =
        prefs.getBoolean(key, fallback)

    override fun set(editor: SharedPreferences.Editor, key: String, data: Boolean) {
        editor.putBoolean(key, data)
    }
}

internal object KPrefIntTransaction : KPrefTransaction<Int> {
    override fun get(prefs: SharedPreferences, key: String, fallback: Int) =
        prefs.getInt(key, fallback)

    override fun set(editor: SharedPreferences.Editor, key: String, data: Int) {
        editor.putInt(key, data)
    }
}

internal object KPrefLongTransaction : KPrefTransaction<Long> {
    override fun get(prefs: SharedPreferences, key: String, fallback: Long) =
        prefs.getLong(key, fallback)

    override fun set(editor: SharedPreferences.Editor, key: String, data: Long) {
        editor.putLong(key, data)
    }
}

internal object KPrefFloatTransaction : KPrefTransaction<Float> {
    override fun get(prefs: SharedPreferences, key: String, fallback: Float) =
        prefs.getFloat(key, fallback)

    override fun set(editor: SharedPreferences.Editor, key: String, data: Float) {
        editor.putFloat(key, data)
    }
}

internal object KPrefStringTransaction : KPrefTransaction<String> {
    override fun get(prefs: SharedPreferences, key: String, fallback: String) =
        prefs.getString(key, fallback)

    override fun set(editor: SharedPreferences.Editor, key: String, data: String) {
        editor.putString(key, data)
    }
}

internal object KPrefSetTransaction : KPrefTransaction<Set<String>> {
    override fun get(prefs: SharedPreferences, key: String, fallback: Set<String>) =
        prefs.getStringSet(key, fallback)!!

    override fun set(editor: SharedPreferences.Editor, key: String, data: Set<String>) {
        editor.putStringSet(key, data)
    }
}
