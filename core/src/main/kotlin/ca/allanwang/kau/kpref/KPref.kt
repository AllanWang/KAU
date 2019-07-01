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

import android.content.Context
import android.content.SharedPreferences
import ca.allanwang.kau.kotlin.ILazyResettable
import ca.allanwang.kau.logging.KL

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Base class for shared preferences
 * All objects extending this class must be called in
 * the app's [android.app.Application] class
 *
 * See the [KPref.kpref] extensions for more details
 *
 * Furthermore, all kprefs are held in the [prefMap],
 * so if you wish to reset a preference, you must also invalidate the kpref
 * from that map
 *
 * You may optionally override [deleteKeys]. This will be called on initialization
 * And delete all keys returned from that method
 */
open class KPref(builder: KPrefBuilder = KPrefBuilderAndroid) : KPrefBuilder by builder {

    lateinit var PREFERENCE_NAME: String
    lateinit var sp: SharedPreferences

    fun initialize(
        c: Context,
        preferenceName: String,
        sharedPrefs: SharedPreferences = c.applicationContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    ) {
        PREFERENCE_NAME = preferenceName
        sp = sharedPrefs
        KL.d { "Shared Preference $preferenceName has been initialized" }
        val toDelete = deleteKeys()
        if (toDelete.isNotEmpty()) {
            val edit = sp.edit()
            toDelete.forEach { edit.remove(it) }
            edit.apply()
        }
    }

    internal val prefMap: MutableMap<String, ILazyResettable<*>> = mutableMapOf()

    fun reset() {
        prefMap.values.forEach { it.invalidate() }
    }

    operator fun get(key: String): ILazyResettable<*>? = prefMap[key]

    open fun deleteKeys(): Array<String> = arrayOf()
}
