package ca.allanwang.kau.kpref

import android.content.Context
import android.content.SharedPreferences
import ca.allanwang.kau.kotlin.ILazyResettable

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
open class KPref {

    lateinit private var c: Context
    lateinit internal var PREFERENCE_NAME: String
    private var initialized = false

    fun initialize(c: Context, preferenceName: String) {
        if (initialized) throw KPrefException("KPref object $preferenceName has already been initialized; please only do so once")
        initialized = true
        this.c = c.applicationContext
        PREFERENCE_NAME = preferenceName
        val toDelete = deleteKeys()
        if (toDelete.isNotEmpty()) {
            val edit = sp.edit()
            toDelete.forEach { edit.remove(it) }
            edit.apply()
        }
    }

    val sp: SharedPreferences by lazy {
        if (!initialized) throw KPrefException("KPref object has not yet been initialized; please initialize it with a context and preference name")
        c.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    internal val prefMap: MutableMap<String, ILazyResettable<*>> = mutableMapOf()

    fun reset() {
        prefMap.values.forEach { it.invalidate() }
    }

    operator fun get(key: String): ILazyResettable<*>? = prefMap[key]

    open fun deleteKeys(): Array<String> = arrayOf()

}