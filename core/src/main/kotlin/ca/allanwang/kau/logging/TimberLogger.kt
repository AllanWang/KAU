@file:Suppress("NOTHING_TO_INLINE")

package ca.allanwang.kau.logging

import android.os.Looper
import android.util.Log


/**
 * Created by Allan Wang on 2017-05-28.
 *
 * Base logger class with a predefined tag
 * This may be extended by an object to effectively replace [Log]
 */
open class KauLogger(val tag: String) {

    var enabled = true
    var showPrivateText = false

    /**
     * Filter pass-through to decide what we wish to log
     * By default, we will ignore verbose and debug logs
     * @returns {@code true} to log the message, {@code false} to ignore
     */
    var filter: (Int) -> Boolean = { it != Log.VERBOSE && it != Log.DEBUG }

    fun disable(disable: Boolean = true): KauLogger {
        enabled = !disable
        return this
    }

    fun debug(enable: Boolean) {
        filter = if (enable) { _ -> true } else { i -> i != Log.VERBOSE && i != Log.DEBUG }
        showPrivateText = enable
    }

    fun log(priority: Int, message: String?, privateMessage: String?, t: Throwable? = null) {
        if (!shouldLog(priority, message, privateMessage, t)) return
        logImpl(priority, message, privateMessage, t)
    }

    protected open fun shouldLog(priority: Int, message: String?, privateMessage: String?, t: Throwable?): Boolean
            = enabled && filter(priority)

    protected open fun logImpl(priority: Int, message: String?, privateMessage: String?, t: Throwable?) {
        var text = message ?: ""
        if (showPrivateText && privateMessage != null)
            text += "\n-\t$privateMessage"
        if (t != null) Log.e(tag, text, t)
        else if (text.isNotBlank()) Log.println(priority, tag, text)
    }

    fun v(text: String?, privateText: String? = null) = log(Log.VERBOSE, text, privateText)
    fun d(text: String?, privateText: String? = null) = log(Log.DEBUG, text, privateText)
    fun i(text: String?, privateText: String? = null) = log(Log.INFO, text, privateText)
    fun e(text: String?, privateText: String? = null) = log(Log.ERROR, text, privateText)
    fun a(text: String?, privateText: String? = null) = log(Log.ASSERT, text, privateText)
    fun e(t: Throwable?, text: String?, privateText: String? = null) = log(Log.ERROR, text, privateText, t)
    fun eThrow(text: String?) {
        if (text != null)
            e(Throwable(text), text)
    }

    /**
     * Log the looper
     */
    fun checkThread(id: Int) {
        val name = Thread.currentThread().name
        val status = if (Looper.myLooper() == Looper.getMainLooper()) "is" else "is not"
        d("$id $status in the main thread - thread name: $name")
    }
}