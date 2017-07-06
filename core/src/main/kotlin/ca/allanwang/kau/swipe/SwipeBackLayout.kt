package ca.allanwang.kau.swipe

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.adjustAlpha
import ca.allanwang.kau.utils.navigationBarColor
import ca.allanwang.kau.utils.statusBarColor
import java.lang.ref.WeakReference

/**
 * The layout that handles all the touch events
 * Note that this differs from [ca.allanwang.kau.widgets.ElasticDragDismissFrameLayout]
 * in that nested scrolling isn't considered
 * If an edge detection occurs, this layout consumes all the touch events
 * Use the [swipeEnabled] toggle if you need the scroll events on the same axis
 */
class SwipeBackLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), SwipeBackContract {

    override val swipeBackLayout: SwipeBackLayout
        get() = this
    /**
     * Threshold of scroll, we will close the activity, when scrollPercent over
     * this value;
     */
    override var scrollThreshold = DEFAULT_SCROLL_THRESHOLD
        set(value) {
            if (value >= 1.0f || value <= 0) throw IllegalArgumentException("Threshold value should be between 0 and 1.0")
            field = value
        }

    var activity: Activity? = null
        set(value) {
            field = value
            if (value != null) {
                statusBarBase = value.statusBarColor
                navBarBase = value.navigationBarColor
            }
        }

    override var swipeEnabled = true

    override var disallowIntercept = false

    private var contentView: View? = null

    private val dragHelper: ViewDragHelper

    private var scrollPercent: Float = 0f

    private var contentOffset: Int = 0

    /**
     * The set of listeners to be sent events through.
     */
    private var listeners: MutableList<WeakReference<SwipeListener>> = mutableListOf()

    private var scrimOpacity: Float = 0f

    override var scrimColor = DEFAULT_SCRIM_COLOR
        /**
         * Set a color to use for the scrim that obscures primary content while a
         * drawer is open.

         * @param color Color to use in 0xAARRGGBB format.
         */
        set(value) {
            field = value
            invalidate()
        }

    private var statusBarBase: Int = 0
    private var navBarBase: Int = 0

    val chromeFadeListener: SwipeListener by lazy {
        object : SwipeListener {
            override fun onScroll(percent: Float, px: Int, edgeFlag: Int) {
                KL.d("PER $percent")
                activity?.apply {
                    statusBarColor = statusBarBase.adjustAlpha(scrimOpacity)
                    navigationBarColor = navBarBase.adjustAlpha(scrimOpacity)
                }
            }

            override fun onEdgeTouch() {}

            override fun onScrollToClose(edgeFlag: Int) {}

        }
    }


    private var inLayout: Boolean = false

    override var edgeSize: Int
        get() = dragHelper.edgeSize
        set(value) {
            dragHelper.edgeSize = value
        }

    override var edgeFlag = SWIPE_EDGE_LEFT
        /**
         * We will verify that only one axis is used at a time
         */
        set(value) {
            if (value !in arrayOf(SWIPE_EDGE_TOP, SWIPE_EDGE_BOTTOM, SWIPE_EDGE_LEFT, SWIPE_EDGE_RIGHT))
                throw SwipeBackException("Edge flag is not valid; use one of the SWIPE_EDGE_* values")
            field = value
            horizontal = edgeFlag == SWIPE_EDGE_LEFT || edgeFlag == SWIPE_EDGE_RIGHT
            dragHelper.setEdgeTrackingEnabled(value)
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

    init {
        dragHelper = ViewDragHelper.create(this, ViewDragCallback())
        val density = resources.displayMetrics.density
        val minVel = MIN_FLING_VELOCITY * density
        //allow touch from anywhere on the screen
        edgeSize = Math.max(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        minVelocity = minVel
//        maxVelocity = 2.5f * minVel
        edgeFlag = edgeFlag
        sensitivity = 0.3f
        addListener(chromeFadeListener)
    }


    /**
     * Set up contentView which will be moved by user gesture

     * @param view
     */
    private fun setContentView(view: View) {
        contentView = view
    }

    override fun setEdgeSizePercent(swipeEdgePercent: Float) {
        edgeSize = (resources.displayMetrics.widthPixels * swipeEdgePercent).toInt()
    }

    /**
     * Add a callback to be invoked when a swipe event is sent to this view.

     * @param listener the swipe listener to attach to this view
     */
    override fun addListener(listener: SwipeListener) {
        listeners.add(WeakReference(listener))
    }

    /**
     * Removes a listener from the set of listeners

     * @param listener
     */
    override fun removeListener(listener: SwipeListener) {
        val iter = listeners.iterator()
        while (iter.hasNext()) {
            val l = iter.next().get()
            if (l == null || l == listener)
                iter.remove()
        }
    }

    /**
     * Scroll out contentView and finish the activity
     */
    override fun scrollToFinishActivity() {
        val childWidth = contentView!!.width
        val top = 0
        val left = childWidth + OVERSCROLL_DISTANCE
        dragHelper.smoothSlideViewTo(contentView, left, top)
        invalidate()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!swipeEnabled || disallowIntercept) return false
        val s = dragHelper.shouldInterceptTouchEvent(event)
        return s
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!swipeEnabled) return false
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
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
        contentView?.layout(xOffset, yOffset, xOffset + contentView!!.measuredWidth, yOffset + contentView!!.measuredHeight)
        inLayout = false
    }

    override fun requestLayout() {
        if (!inLayout) super.requestLayout()
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val drawContent = child === contentView
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
     * Extract content view, and move it from its parent to our view
     * Then add our view to the content frame
     */
    fun attachToActivity(activity: Activity) {
        if (parent != null) return
        this.activity = activity
        val content = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val contentChild = content.getChildAt(0)
        content.removeView(contentChild)
        addView(contentChild)
        setContentView(contentChild)
        content.addView(this)
    }

    /**
     * Remove ourselves from the viewstack
     */
    fun removeFromActivity(activity: Activity) {
        if (parent == null) return
        val contentChild = getChildAt(0)
        val content = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        content.removeView(this)
        removeView(contentChild)
        content.addView(contentChild)
    }

    override fun computeScroll() {
        scrimOpacity = 1 - scrollPercent
        if (dragHelper.continueSettling(true)) ViewCompat.postInvalidateOnAnimation(this)
    }

    private inner class ViewDragCallback : ViewDragHelper.Callback() {
        private var isScrollOverValid: Boolean = false

        /**
         * Toggles whether we may intercept the touch event
         */
        override fun tryCaptureView(view: View, i: Int): Boolean {
            val ret = dragHelper.isEdgeTouched(edgeFlag, i)
            if (ret) {
                listeners.forEach { it.get()?.onEdgeTouch() }
                isScrollOverValid = true
            }
            return ret
        }

        /**
         * Needs to be bigger than 0 to specify that scrolling is possible horizontally
         */
        override fun getViewHorizontalDragRange(child: View): Int {
            return if (horizontal) 1 else 0
        }

        /**
         * Needs to be bigger than 0 to specify that scrolling is possible vertically
         */
        override fun getViewVerticalDragRange(child: View): Int {
            return if (!horizontal) 1 else 0
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            //make sure that we are using the proper axis
            scrollPercent = Math.abs(
                    if (horizontal) left.toFloat() / contentView!!.width
                    else (top.toFloat() / contentView!!.height))
            contentOffset = if (horizontal) left else top
            invalidate()
            if (scrollPercent < scrollThreshold && !isScrollOverValid)
                isScrollOverValid = true

            if (scrollPercent <= 1)
                listeners.forEach { it.get()?.onScroll(scrollPercent, contentOffset, edgeFlag) }

            if (scrollPercent >= 1) {
                if (!(activity?.isFinishing ?: true)) {
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
                //threshold not met; check velocities
                if ((edgeFlag == SWIPE_EDGE_LEFT && xvel > MIN_FLING_VELOCITY)
                        || (edgeFlag == SWIPE_EDGE_RIGHT && xvel < -MIN_FLING_VELOCITY)
                        || (edgeFlag == SWIPE_EDGE_TOP && yvel > MIN_FLING_VELOCITY)
                        || (edgeFlag == SWIPE_EDGE_BOTTOM && yvel < -MIN_FLING_VELOCITY))
                    result = exitCaptureOffsets(edgeFlag, releasedChild)
            } else {
                //threshold met; fling to designated side
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
            return if (edgeFlag == SWIPE_EDGE_RIGHT) Math.min(0, Math.max(left, -child.width))
            else if (edgeFlag == SWIPE_EDGE_LEFT) Math.min(child.width, Math.max(left, 0))
            else 0
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return if (edgeFlag == SWIPE_EDGE_BOTTOM) Math.min(0, Math.max(top, -child.height))
            else if (edgeFlag == SWIPE_EDGE_TOP) Math.min(child.height, Math.max(top, 0))
            else 0
        }
    }

    companion object {
        /**
         * Minimum velocity that will be detected as a fling
         */
        const val MIN_FLING_VELOCITY = 400 // dips per second

        const val DEFAULT_SCRIM_COLOR = 0x99000000.toInt()

        /**
         * Default threshold of scroll
         */
        const val DEFAULT_SCROLL_THRESHOLD = 0.3f

        const val OVERSCROLL_DISTANCE = 10

    }
}
