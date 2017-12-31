@file:Suppress("NOTHING_TO_INLINE")

package ca.allanwang.kau.logging

import android.os.Looper
import android.util.Log

/**
 * Created by Allan Wang on 2017-05-28.
 *
 * Base logger class with a predefined tag
 * This may be extended by an object to effectively replace [Log]
 * Only direct lazy logging is supported, as for best results,
 * applications should extend this and use const/final flags to decide whether logging occurs
 * That way, it will be stripped away by proguard
 *
 * Generally speaking, verbose log may contain private information,
 * as it should be stripped away from production build
 *
 * Debug and info logs may contain sensitive info, and may be differentiated by creating a method such as
 * inline fun _d(message: () -> Any?) {
 *      if (BuildConfig.DEBUG) d(message)
 * }
 */
open class KauLogger(
        /**
         * Tag to be used for each log
         */
        val tag: String,
        /**
         * Toggle to dictate whether a message should be logged
         */
        var shouldLog: (priority: Int) -> Boolean = { true }) {


    inline fun v(message: () -> Any?) = log(Log.VERBOSE, message)

    inline fun i(message: () -> Any?) = log(Log.INFO, message)

    inline fun d(message: () -> Any?) = log(Log.DEBUG, message)

    inline fun e(t: Throwable? = null, message: () -> Any?) = log(Log.ERROR, message, t)

    inline fun eThrow(message: Any?) {
        val msg = message?.toString() ?: return
        log(Log.ERROR, { msg }, Throwable(msg))
    }

    inline fun log(priority: Int, message: () -> Any?, t: Throwable? = null) {
        if (shouldLog(priority))
            logImpl(priority, message()?.toString(), t)
    }

    open fun logImpl(priority: Int, message: String?, t: Throwable?) {
        val msg = message ?: "null"
        if (t != null)
            Log.e(tag, msg, t)
        else
            Log.println(priority, tag, msg)
    }

    /**
     * Log the looper
     */
    inline fun checkThread(id: Int) {
        d {
            val name = Thread.currentThread().name
            val status = if (Looper.myLooper() == Looper.getMainLooper()) "is" else "is not"
            "$id $status in the main thread - thread name: $name"
        }
    }

    fun extend(tag: String) = KauLoggerExtension(tag, this)
}

/**
 * Tag extender for [KauLogger]
 * Will prepend [tag] to any expected log output by [logger]
 * Note that if the parent logger is disabled, the extension logger will not output anything either
 */
class KauLoggerExtension(val tag: String, val logger: KauLogger) {

    inline fun v(message: () -> Any?) = log(Log.VERBOSE, message)

    inline fun i(message: () -> Any?) = log(Log.INFO, message)

    inline fun d(message: () -> Any?) = log(Log.DEBUG, message)

    inline fun e(t: Throwable? = null, message: () -> Any?) = log(Log.ERROR, message, t)

    inline fun eThrow(message: Any?) {
        val msg = message?.toString() ?: return
        log(Log.ERROR, { msg }, Throwable(msg))
    }

    inline fun log(priority: Int, message: () -> Any?, t: Throwable? = null) =
            logger.log(priority, {
                val msg = message()?.toString()
                if (msg == null) null
                else "$tag: $msg"
            }, t)

    inline fun checkThread(id: Int) {
        d {
            val name = Thread.currentThread().name
            val status = if (Looper.myLooper() == Looper.getMainLooper()) "is" else "is not"
            "$id $status in the main thread - thread name: $name"
        }
    }
}
