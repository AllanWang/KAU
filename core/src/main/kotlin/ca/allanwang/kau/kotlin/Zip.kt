package ca.allanwang.kau.kotlin

import org.jetbrains.anko.doAsync
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Allan Wang on 2017-08-06.
 *
 * Collection of zip methods that aim to replicate
 * <a href="http://reactivex.io/documentation/operators/zip.html">Reactive Zips</a>
 * For unit returning functions
 *
 * Typically, the functions will execute asynchronously and call their given callbacks when finished.
 * Once all callbacks are called, the final onFinish callback will be executed.
 *
 * There is also a helper zipper to wrap synchronous functions with Anko's doAsync to achieve the same results
 *
 * Note that not wrapping synchronous functions will render these methods useless,
 * as you can simply define an inline callback after all functions are finished
 */

/**
 * Callback which will only execute the first time
 */
open class ZipCallbackBase {
    var completed: Boolean = false

    inline operator fun invoke(callback: () -> Unit) {
        if (completed) return
        completed = true
        callback()
    }
}

class ZipCallback<T>(val onReceived: (T) -> Unit) : ZipCallbackBase() {
    operator fun invoke(result: T) = invoke { onReceived(result) }
}

class ZipEmptyCallback(val onReceived: () -> Unit) : ZipCallbackBase() {
    operator fun invoke() = invoke(onReceived)
}

/**
 * Given a default result, a series of tasks, and a finished callback,
 * this method will run all tasks and wait until all tasks emit a response
 * The response will then be sent back to the callback
 *
 * ALl tasks must invoke the task callback for [onFinished] to execute
 */
inline fun <reified T> Collection<(ZipCallback<T>) -> Unit>.zip(
    defaultResult: T, crossinline onFinished: (results: Array<T>) -> Unit
) {
    val result = Array(size) { defaultResult }
    val countDown = AtomicInteger(size)
    forEachIndexed { index, asyncFun ->
        asyncFun(ZipCallback {
            result[index] = it
            if (countDown.decrementAndGet() <= 0)
                onFinished(result)
        })
    }
}

/**
 * Simplified zip method with no finished callback arguments
 */
inline fun Collection<(ZipEmptyCallback) -> Unit>.zip(crossinline onFinished: () -> Unit) {
    val countDown = AtomicInteger(size)
    forEach { asyncFun ->
        asyncFun(ZipEmptyCallback {
            if (countDown.decrementAndGet() <= 0)
                onFinished()
        })
    }
}

/**
 * Converts a collection of synchronous tasks to asynchronous tasks with a common callback
 */
inline fun Collection<() -> Unit>.zipAsync(crossinline onFinished: () -> Unit) {
    map { synchronousFun ->
        { callback: ZipEmptyCallback ->
            doAsync {
                synchronousFun()
                callback()
            }; Unit
        }
    }.zip(onFinished)
}
