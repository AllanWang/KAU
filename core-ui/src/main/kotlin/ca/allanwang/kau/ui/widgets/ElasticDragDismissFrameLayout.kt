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
package ca.allanwang.kau.ui.widgets

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.transition.TransitionInflater
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.ui.R
import ca.allanwang.kau.utils.AnimHolder
import ca.allanwang.kau.utils.dimen
import ca.allanwang.kau.utils.isNavBarOnBottom
import ca.allanwang.kau.utils.navigationBarColor
import ca.allanwang.kau.utils.scaleXY
import ca.allanwang.kau.utils.statusBarColor
import ca.allanwang.kau.utils.withAlpha

/**
 * A [FrameLayout] which responds to nested scrolls to create drag-dismissable layouts.
 * Applies an elasticity factor to reduce movement as you approach the given dismiss distance.
 * Optionally also scales down content during drag.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ElasticDragDismissFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    // configurable attribs
    var dragDismissDistance = context.dimen(R.dimen.kau_drag_dismiss_distance)
    var dragDismissFraction = -1f
    var dragDismissScale = 1f
        set(value) {
            field = value
            shouldScale = value != 1f
        }
    private var shouldScale = false
    var dragElacticity = 0.8f

    // state
    private var totalDrag: Float = 0f
    private var draggingDown = false
    private var draggingUp = false
    private var lastEvent = -1

    private var callbacks: MutableList<ElasticDragDismissCallback> = mutableListOf()

    init {
        if (attrs != null) {
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.ElasticDragDismissFrameLayout, 0, 0)
            dragDismissDistance =
                a.getDimensionPixelSize(R.styleable.ElasticDragDismissFrameLayout_dragDismissDistance, Int.MAX_VALUE)
                    .toFloat()
            dragDismissFraction =
                a.getFloat(R.styleable.ElasticDragDismissFrameLayout_dragDismissFraction, dragDismissFraction)
            dragDismissScale = a.getFloat(R.styleable.ElasticDragDismissFrameLayout_dragDismissScale, dragDismissScale)
            dragElacticity = a.getFloat(R.styleable.ElasticDragDismissFrameLayout_dragElasticity, dragElacticity)
            a.recycle()
        }
    }

    abstract class ElasticDragDismissCallback {

        /**
         * Called for each drag event.
         * @param elasticOffset Indicating the drag offset with elasticity applied i.e. may exceed 1.
         *
         * @param elasticOffsetPixels The elastically scaled drag distance in pixels.
         *
         * @param rawOffset Value from [0, 1] indicating the raw drag offset i.e. without elasticity applied.
         * A value of 1 indicates that the dismiss distance has been reached.
         *
         * @param rawOffsetPixels The raw distance the user has dragged
         */
        internal open fun onDrag(
            elasticOffset: Float,
            elasticOffsetPixels: Float,
            rawOffset: Float,
            rawOffsetPixels: Float
        ) {
        }

        /**
         * Called when dragging is released and has exceeded the threshold dismiss distance.
         */
        internal open fun onDragDismissed() {}
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        lastEvent = ev.action
        return super.onInterceptTouchEvent(ev)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes and View.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        // if we're in a drag gesture and the user reverses up the we should take those events
        if (draggingDown && dy > 0 || draggingUp && dy < 0) {
            dragScale(dy)
            consumed[1] = dy
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        dragScale(dyUnconsumed)
    }

    override fun onStopNestedScroll(child: View) {
        if (Math.abs(totalDrag) >= dragDismissDistance) {
            dispatchDismissCallback()
        } else { // settle back to natural position

            if (lastEvent == MotionEvent.ACTION_DOWN) {
                translationY = 0f
                scaleXY = 1f
            } else {
                animate()
                    .translationY(0f)
                    .scaleXY(1f)
                    .setDuration(200L)
                    .setInterpolator(AnimHolder.fastOutSlowInInterpolator(context))
                    .setListener(null)
                    .start()
            }

            totalDrag = 0f
            draggingUp = false
            draggingDown = draggingUp
            dispatchDragCallback(0f, 0f, 0f, 0f)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (dragDismissFraction > 0f) {
            dragDismissDistance = h * dragDismissFraction
        }
    }

    fun addListener(listener: ElasticDragDismissCallback) {
        callbacks.add(listener)
    }

    fun removeListener(listener: ElasticDragDismissCallback) {
        callbacks.remove(listener)
    }

    private fun dragScale(scroll: Int) {
        if (scroll == 0) return

        totalDrag += scroll.toFloat()

        // track the direction & set the pivot point for scaling
        // don't double track i.e. if start dragging down and then reverse, keep tracking as
        // dragging down until they reach the 'natural' position
        if (scroll < 0 && !draggingUp && !draggingDown) {
            draggingDown = true
            if (shouldScale) pivotY = height.toFloat()
        } else if (scroll > 0 && !draggingDown && !draggingUp) {
            draggingUp = true
            if (shouldScale) pivotY = 0f
        }
        // how far have we dragged relative to the distance to perform a dismiss
        // (0–1 where 1 = dismiss distance). Decreasing logarithmically as we approach the limit
        var dragFraction = Math.log10((1 + Math.abs(totalDrag) / dragDismissDistance).toDouble()).toFloat()

        // calculate the desired translation given the drag fraction
        var dragTo = dragFraction * dragDismissDistance * dragElacticity

        if (draggingUp) {
            // as we use the absolute magnitude when calculating the drag fraction, need to
            // re-apply the drag direction
            dragTo *= -1f
        }
        translationY = dragTo

        if (shouldScale) {
            scaleXY = 1 - (1 - dragDismissScale) * dragFraction
        }

        // if we've reversed direction and gone past the settle point then clear the flags to
        // allow the list to get the scroll events & reset any transforms
        if (draggingDown && totalDrag >= 0 || draggingUp && totalDrag <= 0) {
            dragFraction = 0f
            dragTo = dragFraction
            totalDrag = dragTo
            draggingUp = false
            draggingDown = draggingUp
            translationY = 0f
            scaleXY = 1f
        }
        dispatchDragCallback(
            dragFraction, dragTo,
            Math.min(1f, Math.abs(totalDrag) / dragDismissDistance), totalDrag
        )
    }

    private fun dispatchDragCallback(
        elasticOffset: Float,
        elasticOffsetPixels: Float,
        rawOffset: Float,
        rawOffsetPixels: Float
    ) {
        callbacks.forEach {
            it.onDrag(
                elasticOffset, elasticOffsetPixels,
                rawOffset, rawOffsetPixels
            )
        }
    }

    private fun dispatchDismissCallback() {
        callbacks.forEach { it.onDragDismissed() }
    }

    /**
     * An [ElasticDragDismissCallback] which fades system chrome (i.e. status bar and
     * navigation bar) whilst elastic drags are performed and
     * [finishes][Activity.finishAfterTransition] the activity when drag dismissed.
     */
    open class SystemChromeFader(private val activity: Activity) : ElasticDragDismissCallback() {
        private val statusBarAlpha: Int = Color.alpha(activity.statusBarColor)
        private val navBarAlpha: Int = Color.alpha(activity.navigationBarColor)
        private val fadeNavBar: Boolean = activity.isNavBarOnBottom

        public override fun onDrag(
            elasticOffset: Float,
            elasticOffsetPixels: Float,
            rawOffset: Float,
            rawOffsetPixels: Float
        ) {
            if (elasticOffsetPixels > 0) {
                // dragging downward, fade the status bar in proportion
                activity.statusBarColor = activity.statusBarColor.withAlpha(((1f - rawOffset) * statusBarAlpha).toInt())
            } else if (elasticOffsetPixels == 0f) {
                // reset
                activity.statusBarColor = activity.statusBarColor.withAlpha(statusBarAlpha)
                activity.navigationBarColor = activity.navigationBarColor.withAlpha(navBarAlpha)
            } else if (fadeNavBar) {
                // dragging upward, fade the navigation bar in proportion
                activity.navigationBarColor =
                    activity.navigationBarColor.withAlpha(((1f - rawOffset) * navBarAlpha).toInt())
            }
        }

        public override fun onDragDismissed() {
            activity.finishAfterTransition()
        }
    }

    fun addExitListener(
        activity: Activity,
        transitionBottom: Int = R.transition.kau_exit_slide_bottom,
        transitionTop: Int = R.transition.kau_exit_slide_top
    ) {
        addListener(object : ElasticDragDismissFrameLayout.SystemChromeFader(activity) {
            override fun onDragDismissed() {
                KL.v { "New transition" }
                activity.window.returnTransition = TransitionInflater.from(activity)
                    .inflateTransition(if (translationY > 0) transitionBottom else transitionTop)
                activity.finishAfterTransition()
            }
        })
    }
}
