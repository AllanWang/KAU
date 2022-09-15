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
package ca.allanwang.kau.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import androidx.annotation.VisibleForTesting
import ca.allanwang.kau.kotlin.kauRemoveIf

/**
 * Created by Allan Wang on 2017-11-10.
 *
 * Wrapper for value animator specifically dealing with progress values This is typically a float
 * range of 0 to 1, but can be customized This differs in that everything can be done with simple
 * listeners, which will be bundled and added to the backing [ValueAnimator]
 */
class ProgressAnimator private constructor() : ValueAnimator() {

  companion object {

    fun ofFloat(builder: ProgressAnimator.() -> Unit = {}): ProgressAnimator =
      ProgressAnimator()
        .apply {
          setFloatValues(0f, 1f)
          addUpdateListener { apply(it.animatedValue as Float) }
          addListener(
            object : AnimatorListenerAdapter() {

              override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
                isCancelled = false
                startActions.runAll()
              }

              override fun onAnimationCancel(animation: Animator) {
                isCancelled = true
                cancelActions.runAll()
              }

              override fun onAnimationEnd(animation: Animator) {
                endActions.runAll()
                isCancelled = false
              }
            }
          )
        }
        .apply(builder)

    /**
     * Gets output of a linear function starting at [start] when [progress] is 0 and [end] when
     * [progress] is 1 at point [progress].
     */
    fun progress(start: Float, end: Float, progress: Float): Float =
      start + (end - start) * progress

    fun progress(start: Float, end: Float, progress: Float, min: Float, max: Float): Float =
      when {
        min == max ->
          throw IllegalArgumentException("Progress range cannot be 0 (min == max == $min")
        progress <= min -> start
        progress >= max -> end
        else -> {
          val trueProgress = (progress - min) / (max - min)
          start + (end - start) * trueProgress
        }
      }

    /** Progress variant that takes in and returns int */
    fun progress(start: Int, end: Int, progress: Float): Int =
      (start + (end - start) * progress).toInt()
  }

  private val animators: MutableList<ProgressDisposableAction> = mutableListOf()
  @VisibleForTesting
  internal val startActions: MutableList<ProgressDisposableRunnable> = mutableListOf()
  @VisibleForTesting
  internal val cancelActions: MutableList<ProgressDisposableRunnable> = mutableListOf()
  @VisibleForTesting
  internal val endActions: MutableList<ProgressDisposableRunnable> = mutableListOf()
  var isCancelled: Boolean = false
    private set

  val animatorCount
    get() = animators.size

  /** Converts an action to a disposable action */
  private fun <T> ((T) -> Unit).asDisposable(): (T) -> Boolean = {
    this(it)
    false
  }

  private fun ProgressRunnable.asDisposable(): ProgressDisposableRunnable = {
    this()
    false
  }

  /**
   * If [condition] applies, run the animator.
   * @return [condition]
   */
  private fun ProgressAction.runIf(condition: Boolean, progress: Float): Boolean {
    if (condition) {
      this(progress)
    }
    return condition
  }

  @VisibleForTesting
  internal fun MutableList<ProgressDisposableRunnable>.runAll() = kauRemoveIf { it() }

  @VisibleForTesting
  internal fun apply(progress: Float) {
    animators.kauRemoveIf { it(progress) }
  }

  fun withAnimator(action: ProgressAction) = withDisposableAnimator(action.asDisposable())

  /** Range animator. Multiples the range by the current float progress before emission */
  fun withAnimator(from: Float, to: Float, action: ProgressAction) =
    withDisposableAnimator(from, to, action.asDisposable())

  fun withAnimator(from: Int, to: Int, action: ProgressIntAction) =
    withDisposableAnimator(from, to, action.asDisposable())

  fun withDisposableAnimator(action: ProgressDisposableAction) = animators.add(action)

  fun withDisposableAnimator(from: Float, to: Float, action: ProgressDisposableAction) {
    if (to != from) {
      animators.add { action(progress(from, to, it)) }
    }
  }

  fun withDisposableAnimator(from: Int, to: Int, action: ProgressIntDisposableAction) {
    if (to != from) {
      animators.add { action(progress(from, to, it)) }
    }
  }

  fun withRangeAnimator(min: Float, max: Float, start: Float, end: Float, action: ProgressAction) {
    require(min < max) { "Range animator must have min < max; currently min=$min, max=$max" }
    withDisposableAnimator {
      when {
        it > max -> {
          action(end) // apply action in case frames were skipped
          true
        }
        it < min -> false
        else -> {
          action(progress(start, end, it, min, max))
          false
        }
      }
    }
  }

  @Deprecated(
    level = DeprecationLevel.WARNING,
    message = "Renamed to withPointAction",
    replaceWith = ReplaceWith("withPointAction(point, action)")
  )
  fun withPointAnimator(point: Float, action: ProgressAction) =
    withPointAction(point, isAscendingProgress = true, action = action)

  fun withPointAction(point: Float, isAscendingProgress: Boolean = true, action: ProgressAction) {
    animators.add {
      action.runIf(
        (isAscendingProgress && (it >= point) || !isAscendingProgress && (it <= point)),
        it
      )
    }
  }

  fun withDelayedStartAction(skipCount: Int, action: ProgressAction) {
    var count = 0
    withStartAction { count = 0 }
    animators.add { action.runIf(count++ >= skipCount, it) }
  }

  /** Start action to be called once when the animator first begins */
  fun withStartAction(action: ProgressRunnable) = withDisposableStartAction(action.asDisposable())

  fun withDisposableStartAction(action: ProgressDisposableRunnable) = startActions.add(action)

  fun withCancelAction(action: ProgressRunnable) = withDisposableCancelAction(action.asDisposable())

  fun withDisposableCancelAction(action: ProgressDisposableRunnable) = cancelActions.add(action)

  fun withEndAction(action: ProgressRunnable) = withDisposableEndAction(action.asDisposable())

  fun withDisposableEndAction(action: ProgressDisposableRunnable) = endActions.add(action)

  fun reset() {
    if (isRunning) {
      cancel()
    }
    animators.clear()
    startActions.clear()
    cancelActions.clear()
    endActions.clear()
    isCancelled = false
  }
}

private typealias ProgressAction = (Float) -> Unit

private typealias ProgressDisposableAction = (Float) -> Boolean

private typealias ProgressIntAction = (Int) -> Unit

private typealias ProgressIntDisposableAction = (Int) -> Boolean

private typealias ProgressRunnable = () -> Unit

private typealias ProgressDisposableRunnable = () -> Boolean
