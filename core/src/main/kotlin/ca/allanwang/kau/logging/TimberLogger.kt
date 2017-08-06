@file:Suppress("NOTHING_TO_INLINE")

package ca.allanwang.kau.logging

import android.util.Log


/**
 * Created by Allan Wang on 2017-05-28.
 */
open class KauLogger(val tag: String) {

    var enabled = true

    /**
     * Filter pass-through to decide what we wish to log
     * By default, we will ignore verbose and debug logs
     */
    var filter: (Int) -> Boolean = { it != Log.VERBOSE && it != Log.DEBUG }

    fun disable(): KauLogger {
        enabled = false
        return this
    }

    fun debug(enable: Boolean) {
        filter = if (enable) { _ -> true } else { i -> i != Log.VERBOSE && i != Log.DEBUG }
    }

    protected fun log(priority: Int, message: String?, t: Throwable? = null) {
        if (!enabled || !filter(priority)) return
        if (t != null) Log.e(tag, message, t)
        else if (message != null) Log.println(priority, tag, message)
    }

    fun v(text: String?) = log(Log.VERBOSE, text)
    fun d(text: String?) = log(Log.DEBUG, text)
    fun d(text: String?, info: String?) {
        d(text)
        i(info)
    }

    fun i(text: String?) = log(Log.INFO, text)
    fun e(text: String?, t: Throwable? = null) = log(Log.ERROR, text, t)
    fun eThrow(text: String?) = e(text, Throwable(text))
    fun a(text: String?) = log(Log.ASSERT, text)
}