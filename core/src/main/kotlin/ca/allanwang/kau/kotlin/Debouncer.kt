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
package ca.allanwang.kau.kotlin

import ca.allanwang.kau.logging.KL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created by Allan Wang on 2017-08-05.
 *
 * Thread safe function wrapper to allow for debouncing With reference to <a
 * href="https://stackoverflow.com/a/20978973/4407321">Stack Overflow</a>
 */

/**
 * The debouncer base Implements everything except for the callback, as the number of variables is
 * different between implementations You may still use this without extending it, but you'll have to
 * pass a callback each time
 */
open class Debouncer(var interval: Long) {
  private val sched = Executors.newScheduledThreadPool(1)
  private var task: DebounceTask? = null

  /**
   * Generic invocation to pass a callback to the new task Pass a new callback for the task If
   * another task is pending, it will be invalidated
   */
  operator fun invoke(callback: () -> Unit) {
    synchronized(this) {
      task?.invalidate()
      val newTask = DebounceTask(callback)
      KL.v { "Debouncer task created: $newTask in $this" }
      sched.schedule(newTask, interval, TimeUnit.MILLISECONDS)
      task = newTask
    }
  }

  /**
   * Call to cancel all pending requests and shutdown the thread pool The debouncer cannot be used
   * after this
   */
  fun terminate() {
    task?.invalidate()
    task = null
    sched.shutdownNow()
  }

  /** Invalidate any pending tasks */
  fun cancel() {
    synchronized(this) {
      if (task != null) KL.v { "Debouncer cancelled for $task in $this" }
      task?.invalidate()
      task = null
    }
  }
}

/*
 * Helper extensions for functions with 0 to 3 arguments
 */

/**
 * The debounced task Holds a callback to execute if the time has come and it is still valid All
 * methods can be viewed as synchronous as the invocation is synchronous
 */
private class DebounceTask(inline val callback: () -> Unit) : Runnable {
  private var valid = true

  fun invalidate() {
    valid = false
  }

  override fun run() {
    if (!valid) return
    valid = false
    KL.v { "Debouncer task executed $this" }
    try {
      callback()
    } catch (e: Exception) {
      KL.e(e) { "DebouncerTask exception" }
    }
  }
}

/** A zero input debouncer */
class Debouncer0 internal constructor(interval: Long, val callback: () -> Unit) :
    Debouncer(interval) {
  operator fun invoke() = invoke(callback)
}

fun debounce(interval: Long, callback: () -> Unit) = Debouncer0(interval, callback)

fun (() -> Unit).debounce(interval: Long) = debounce(interval, this)

/** A one argument input debouncer */
class Debouncer1<T> internal constructor(interval: Long, val callback: (T) -> Unit) :
    Debouncer(interval) {
  operator fun invoke(key: T) = invoke { callback(key) }
}

fun <T> debounce(interval: Long, callback: (T) -> Unit) = Debouncer1(interval, callback)

fun <T> ((T) -> Unit).debounce(interval: Long) = debounce(interval, this)

/** A two argument input debouncer */
class Debouncer2<T, V> internal constructor(interval: Long, val callback: (T, V) -> Unit) :
    Debouncer(interval) {
  operator fun invoke(arg0: T, arg1: V) = invoke { callback(arg0, arg1) }
}

fun <T, V> debounce(interval: Long, callback: (T, V) -> Unit) = Debouncer2(interval, callback)

fun <T, V> ((T, V) -> Unit).debounce(interval: Long) = debounce(interval, this)

/** A three argument input debouncer */
class Debouncer3<T, U, V> internal constructor(interval: Long, val callback: (T, U, V) -> Unit) :
    Debouncer(interval) {
  operator fun invoke(arg0: T, arg1: U, arg2: V) = invoke { callback(arg0, arg1, arg2) }
}

fun <T, U, V> debounce(interval: Long, callback: ((T, U, V) -> Unit)) =
    Debouncer3(interval, callback)

fun <T, U, V> ((T, U, V) -> Unit).debounce(interval: Long) = debounce(interval, this)
