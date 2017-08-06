@file:Suppress("NOTHING_TO_INLINE")

package ca.allanwang.kau.logging

import timber.log.Timber


/**
 * Created by Allan Wang on 2017-05-28.
 *
 * Timber extension that will embed the tag as part of the message for each log item
 */
open class TimberLogger(tag: String) {
    val TAG = "$tag: %s"
    inline fun e(s: String) = Timber.e(TAG, s)
    inline fun e(t: Throwable?, s: String = "error") = if (t == null) e(s) else Timber.e(t, TAG, s)
    inline fun d(s: String) = Timber.d(TAG, s)
    inline fun i(s: String) = Timber.i(TAG, s)
    inline fun v(s: String) = Timber.v(TAG, s)
    inline fun eThrow(s: String) = e(Throwable(s))
//    fun plant() {
//     Timber.plant(Timber.Tree())
//    }
}