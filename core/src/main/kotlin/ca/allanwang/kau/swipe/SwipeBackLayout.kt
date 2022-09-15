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
package ca.allanwang.kau.swipe

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.adjustAlpha
import ca.allanwang.kau.utils.navigationBarColor
import ca.allanwang.kau.utils.statusBarColor
import java.lang.ref.WeakReference
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * The layout that handles all the touch events Note that this differs from
 * [ca.allanwang.kau.ui.widgets.ElasticDragDismissFrameLayout] in that nested scrolling isn't
 * considered If an edge detection occurs, this layout consumes all the touch events Use the
 * [swipeEnabled] toggle if you need the scroll events on the same axis
 */
internal class SwipeBackLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    FrameLayout(context, attrs, defStyle), SwipeBackContract, SwipeBackContractInternal {

  override val swipeBackLayout: SwipeBackLayout
    get() = this
  /** Threshold of scroll, we will close the activity, when scrollPercent over this value */
  override var scrollThreshold = DEFAULT_SCROLL_THRESHOLD
    set(value) {
      if (value >= 1.0f || value <= 0f) {
        throw IllegalArgumentException("Threshold value should be between 0.0 and 1.0")
      }
      field = value
    }

  var activity: Activity? = null
    @SuppressLint("NewApi")
    set(value) {
      field = value
      if (value != null) {
        statusBarBase = value.statusBarColor
        navBarBase = value.navigationBarColor
      }
    }

  override var swipeEnabled = true

  override var disallowIntercept = false

  private lateinit var contentViewRef: WeakReference<View>

  private val dragHelper: ViewDragHelper

  private var scrollPercent: Float = 0f

  private var contentOffset: Int = 0

  /** The set of listeners to be sent events through. */
  private var listeners: MutableList<WeakReference<SwipeListener>> = mutableListOf()

  private var scrimOpacity: Float = 0f

  override var scrimColor = DEFAULT_SCRIM_COLOR
    /**
     * Set a color to use for the scrim that obscures primary content while a drawer is open.
     *
     * @param color Color to use in 0xAARRGGBB format.
     */
    set(value) {
      field = value
      invalidate()
    }

  private var statusBarBase: Int = 0
  private var navBarBase: Int = 0

  private val chromeFadeListener: SwipeListener =
      object : SwipeListener {
        override fun onScroll(percent: Float, px: Int, edgeFlag: Int) {
          if (!transitionSystemBars) return
          activity?.apply {
            statusBarColor = statusBarBase.adjustAlpha(scrimOpacity)
            navigationBarColor = navBarBase.adjustAlpha(scrimOpacity)
          }
        }

        override fun onEdgeTouch() {}

        override fun onScrollToClose(edgeFlag: Int) {}
      }

  private var inLayout: Boolean = false

  override var edgeSize: Int
    get() = dragHelper.edgeSize
    set(value) {
      dragHelper.edgeSize = value
    }

  override var edgeFlag = SWIPE_EDGE_LEFT
    /** We will verify that only one axis is used at a time */
    set(value) {
      if (value !in arrayOf(SWIPE_EDGE_TOP, SWIPE_EDGE_BOTTOM, SWIPE_EDGE_LEFT, SWIPE_EDGE_RIGHT)) {
        throw IllegalArgumentException("Edge flag is not valid; use one of the SWIPE_EDGE_* values")
      }
      field = value
      horizontal = edgeFlag == SWIPE_EDGE_LEFT || edgeFlag == SWIPE_EDGE_RIGHT
      dragHelper.setEdgeTrackingEnabled(value)
      dragHelper.edgeFlag = value
    }

  private var horizontal = true

  override var minVelocity: Float
    get() = dragHelper.minVelocity
    set(value) {
      dragHelper.minVelocity = value
    }

  override var maxVelocity: Float
    get() = dragHelper.maxVelocity
    set(value) {
      dragHelper.maxVelocity = value
    }

  override var sensitivity: Float
    get() = dragHelper.sensitivity
    set(value) {
      dragHelper.setSensitivity(context, value)
    }

  override var transitionSystemBars: Boolean = true

  init {
    dragHelper = ViewDragHelper.create(this, ViewDragCallback())
    // allow touch from anywhere on the screen
    edgeSize = max(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
    edgeFlag = edgeFlag
    sensitivity = 0.3f
    addListener(chromeFadeListener)
  }

  override fun setEdgeSizePercent(swipeEdgePercent: Float) {
    edgeSize =
        ((if (horizontal) resources.displayMetrics.widthPixels
            else resources.displayMetrics.heightPixels) * swipeEdgePercent)
            .toInt()
  }

  /**
   * Add a callback to be invoked when a swipe event is sent to this view.
   *
   * @param listener the swipe listener to attach to this view
   */
  override fun addListener(listener: SwipeListener) {
    listeners.add(WeakReference(listener))
  }

  /**
   * Removes a listener from the set of listeners and scans our list for invalid ones
   *
   * @param listener
   */
  override fun removeListener(listener: SwipeListener) {
    val iter = listeners.iterator()
    while (iter.hasNext()) {
      val l = iter.next().get()
      if (l == null || l == listener) {
        iter.remove()
      }
    }
  }

  /** Checks if a listener exists in our list, and remove invalid ones at the same time */
  override fun hasListener(listener: SwipeListener): Boolean {
    val iter = listeners.iterator()
    while (iter.hasNext()) {
      val l = iter.next().get()
      if (l == null) {
        iter.remove()
      } else if (l == listener) {
        return true
      }
    }
    return false
  }

  /** Scroll out contentView and finish the activity */
  override fun scrollToFinishActivity() {
    val contentView =
        contentViewRef.get()
            ?: return KL.e {
              "KauSwipe cannot scroll to finish as contentView is null. Is onPostCreate called?"
            }
    val swipeWidth = contentView.width + OVERSCROLL_DISTANCE
    val swipeHeight = contentView.height + OVERSCROLL_DISTANCE
    var top = 0
    var left = 0
    when (edgeFlag) {
      SWIPE_EDGE_LEFT -> left = swipeWidth
      SWIPE_EDGE_TOP -> top = swipeHeight
      SWIPE_EDGE_RIGHT -> left = -swipeWidth
      SWIPE_EDGE_BOTTOM -> top = -swipeHeight
    }
    dragHelper.smoothSlideViewTo(contentView, left, top)
    invalidate()
  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    if (!swipeEnabled || disallowIntercept) return false
    return try {
      dragHelper.shouldInterceptTouchEvent(event)
    } catch (e: Exception) {
      false
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (!swipeEnabled || disallowIntercept) return false
    try {
      dragHelper.processTouchEvent(event)
    } catch (e: Exception) {
      return false
    }
    return true
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    val contentView =
        contentViewRef.get()
            ?: return KL.e {
              "KauSwipe cannot change layout as contentView is null. Is onPostCreate called?"
            }
    inLayout = true
    val xOffset: Int
    val yOffset: Int
    if (horizontal) {
      xOffset = contentOffset
      yOffset = 0
    } else {
      xOffset = 0
      yOffset = contentOffset
    }
    contentView.layout(
        xOffset, yOffset, xOffset + contentView.measuredWidth, yOffset + contentView.measuredHeight)
    inLayout = false
  }

  override fun requestLayout() {
    if (!inLayout) super.requestLayout()
  }

  override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
    val drawContent = child === contentViewRef.get()
    val ret = super.drawChild(canvas, child, drawingTime)
    if (scrimOpacity > 0 && drawContent && dragHelper.viewDragState != ViewDragHelper.STATE_IDLE)
        drawScrim(canvas, child)
    return ret
  }

  private fun drawScrim(canvas: Canvas, child: View) {
    val color = scrimColor.adjustAlpha(scrimOpacity)
    when (edgeFlag) {
      SWIPE_EDGE_LEFT -> canvas.clipRect(0, 0, child.left, height)
      SWIPE_EDGE_RIGHT -> canvas.clipRect(child.right, 0, width, height)
      SWIPE_EDGE_TOP -> canvas.clipRect(0, 0, width, child.top)
      SWIPE_EDGE_BOTTOM -> canvas.clipRect(0, child.bottom, width, height)
    }
    canvas.drawColor(color)
  }

  /**
   * Extract content view, and move it from its parent to our view Then add our view to the content
   * frame
   */
  fun attachToActivity(activity: Activity) {
    if (parent != null) return
    this.activity = activity
    val content = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
    val contentChild = content.getChildAt(0)
    content.removeView(contentChild)
    addView(contentChild)
    contentViewRef = WeakReference(contentChild)
    content.addView(this)
  }

  /** Remove ourselves from the viewstack */
  fun removeFromActivity(activity: Activity) {
    if (parent == null) return
    val contentChild = getChildAt(0)
    val content = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
    content.removeView(this)
    removeView(contentChild)
    content.addView(contentChild)
    contentViewRef.clear()
  }

  override fun computeScroll() {
    scrimOpacity = 1 - scrollPercent
    if (dragHelper.continueSettling(true)) ViewCompat.postInvalidateOnAnimation(this)
  }

  private inner class ViewDragCallback : ViewDragHelper.Callback() {
    private var isScrollOverValid: Boolean = false

    /** Toggles whether we may intercept the touch event */
    override fun tryCaptureView(view: View, i: Int): Boolean {
      val ret = dragHelper.isEdgeTouched(edgeFlag, i)
      if (ret) {
        listeners.forEach { it.get()?.onEdgeTouch() }
        isScrollOverValid = true
      }
      return ret
    }

    /** Needs to be bigger than 0 to specify that scrolling is possible horizontally */
    override fun getViewHorizontalDragRange(child: View): Int {
      return if (horizontal) 1 else 0
    }

    /** Needs to be bigger than 0 to specify that scrolling is possible vertically */
    override fun getViewVerticalDragRange(child: View): Int {
      return if (!horizontal) 1 else 0
    }

    override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
      super.onViewPositionChanged(changedView, left, top, dx, dy)
      val contentView =
          contentViewRef.get()
              ?: return KL.e {
                "KauSwipe cannot change view position as contentView is null; is onPostCreate called?"
              }
      // make sure that we are using the proper axis
      scrollPercent =
          abs(
              if (horizontal) left.toFloat() / contentView.width
              else (top.toFloat() / contentView.height))
      contentOffset = if (horizontal) left else top
      invalidate()
      if (scrollPercent < scrollThreshold && !isScrollOverValid) isScrollOverValid = true

      if (scrollPercent <= 1)
          listeners.forEach { it.get()?.onScroll(scrollPercent, contentOffset, edgeFlag) }

      if (scrollPercent >= 1) {
        if (activity?.isFinishing == false) {
          if (scrollPercent >= scrollThreshold && isScrollOverValid) {
            isScrollOverValid = false
            listeners.forEach { it.get()?.onScrollToClose(edgeFlag) }
          }
          activity?.finish()
          activity?.overridePendingTransition(0, 0)
        }
      }
    }

    override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
      var result = Pair(0, 0)
      if (scrollPercent <= scrollThreshold) {
        // threshold not met; check velocities
        if ((edgeFlag == SWIPE_EDGE_LEFT && xvel > minVelocity) ||
            (edgeFlag == SWIPE_EDGE_RIGHT && xvel < -minVelocity) ||
            (edgeFlag == SWIPE_EDGE_TOP && yvel > minVelocity) ||
            (edgeFlag == SWIPE_EDGE_BOTTOM && yvel < -minVelocity))
            result = exitCaptureOffsets(edgeFlag, releasedChild)
      } else {
        // threshold met; fling to designated side
        result = exitCaptureOffsets(edgeFlag, releasedChild)
      }
      dragHelper.settleCapturedViewAt(result.first, result.second)
      invalidate()
    }

    private fun exitCaptureOffsets(edgeFlag: Int, view: View): Pair<Int, Int> {
      var top: Int = 0
      var left: Int = 0
      when (edgeFlag) {
        SWIPE_EDGE_LEFT -> left = view.width + OVERSCROLL_DISTANCE
        SWIPE_EDGE_RIGHT -> left = -(view.width + OVERSCROLL_DISTANCE)
        SWIPE_EDGE_TOP -> top = view.height + OVERSCROLL_DISTANCE
        SWIPE_EDGE_BOTTOM -> top = -(view.height + OVERSCROLL_DISTANCE)
      }
      return Pair(left, top)
    }

    override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
      return when (edgeFlag) {
        SWIPE_EDGE_RIGHT -> min(0, max(left, -child.width))
        SWIPE_EDGE_LEFT -> min(child.width, max(left, 0))
        else -> 0
      }
    }

    override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
      return when (edgeFlag) {
        SWIPE_EDGE_BOTTOM -> min(0, max(top, -child.height))
        SWIPE_EDGE_TOP -> min(child.height, max(top, 0))
        else -> 0
      }
    }
  }

  companion object {

    const val DEFAULT_SCRIM_COLOR = 0x99000000.toInt()

    /** Default threshold of scroll */
    const val DEFAULT_SCROLL_THRESHOLD = 0.3f

    const val OVERSCROLL_DISTANCE = 10
  }
}
