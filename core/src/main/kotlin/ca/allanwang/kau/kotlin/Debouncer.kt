package ca.allanwang.kau.kotlin

import ca.allanwang.kau.logging.KL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by Allan Wang on 2017-08-05.
 *
 * Thread safe function wrapper to allow for debouncing
 * With reference to <a href="https://stackoverflow.com/a/20978973/4407321">Stack Overflow</a>
 */
class Debouncer<T>(var interval: Long, val callback: (T) -> Any) {
    private val sched = Executors.newScheduledThreadPool(1)
    private var task: TimerTask? = null

    /**
     * Pass a new key to the debouncer
     * If an existing task exists, it will be invalidated
     */
    operator fun invoke(key: T) {
        synchronized(this) {
            task?.invalidate()
            val newTask = TimerTask(key)  //ensure we are passing a nonnull command
            sched.schedule(newTask, interval, TimeUnit.MILLISECONDS)
            task = newTask
        }
    }

    /**
     * Call to cancel all pending requests and shutdown the thread pool
     */
    fun terminate() = sched.shutdownNow()

    // The task that wakes up when the wait time elapses
    private inner class TimerTask(private val key: T) : Runnable {
        private var valid = true

        fun invalidate() {
            synchronized(this) {
                valid = false
            }
        }

        override fun run() {
            synchronized(this) {
                if (valid) {
                    try {
                        callback(key)
                    } catch (e: Exception) {
                        KL.e(e, "Debouncer exception")
                    }
                }
            }
        }
    }
}

/**
 * A debouncing variant with no arguments
 */
class EmptyDebouncer(var interval: Long, val callback: () -> Any) {
    private val sched = Executors.newScheduledThreadPool(1)
    private var task: TimerTask? = null

    /**
     * Pass a new key to the debouncer
     * If an existing task exists, it will be invalidated
     */
    operator fun invoke() {
        synchronized(this) {
            task?.invalidate()
            val newTask = TimerTask()  //ensure we are passing a nonnull command
            sched.schedule(newTask, interval, TimeUnit.MILLISECONDS)
            task = newTask
        }
    }

    /**
     * Call to cancel all pending requests and shutdown the thread pool
     */
    fun terminate() = sched.shutdownNow()

    // The task that wakes up when the wait time elapses
    private inner class TimerTask : Runnable {
        private var valid = true

        fun invalidate() {
            synchronized(this) {
                valid = false
            }
        }

        override fun run() {
            synchronized(this) {
                if (valid) {
                    try {
                        callback()
                    } catch (e: Exception) {
                        KL.e(e, "EmptyDebouncer exception")
                    }
                }
            }
        }
    }
}

/**
 * Wraps a function so that it can be debounced
 */
fun <T> ((T) -> Any).debounce(interval: Long)
        = Debouncer<T>(interval, this)

fun (() -> Any).debounce(interval: Long)
        = EmptyDebouncer(interval, this)