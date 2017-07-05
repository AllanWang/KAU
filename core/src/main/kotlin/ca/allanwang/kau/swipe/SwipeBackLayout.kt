package ca.allanwang.kau.swipe

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import ca.allanwang.kau.utils.navigationBarColor
import ca.allanwang.kau.utils.statusBarColor
import java.lang.ref.WeakReference

class SwipeBackLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), SwipeBackPageContract {

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
            statusBarAlpha = Color.alpha(value.statusBarColor)
            navBarAlpha = Color.alpha(value.navigationBarColor)
            }
        }

    override var swipeEnabled = true

    override var disallowIntercept = false

    private var contentView: View? = null

    private val dragHelper: ViewDragHelper

    private var scrollPercent: Float = 0.toFloat()

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

    var statusBarAlpha: Int = 255
    var navBarAlpha: Int = 255

    val chromeFadeListener: SwipeListener by lazy {
        object : SwipeListener {
            override fun onScroll(percent: Float, px: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onEdgeTouch() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onScrollToClose() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }


    private var inLayout: Boolean = false

    /**
     * Edge being dragged
     */
    private var trackingEdge: Int = 0

    init {
        dragHelper = ViewDragHelper.create(this, ViewDragCallback())
        val density = resources.displayMetrics.density
        val minVel = MIN_FLING_VELOCITY * density
        setEdgeSize(resources.displayMetrics.widthPixels)
        dragHelper.minVelocity = minVel
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT)
    }


    /**
     * Set up contentView which will be moved by user gesture

     * @param view
     */
    private fun setContentView(view: View) {
        contentView = view
    }

    /**
     * Set the size of an edge. This is the range in pixels along the edges of
     * this view that will actively detect edge touches or drags if edge
     * tracking is enabled.

     * @param swipeEdge The size of an edge in pixels
     */
    override fun setEdgeSize(swipeEdge: Int) {
        trackingEdge = swipeEdge
    }


    override fun setEdgeSizePercent(swipeEdgePercent: Float) {
        trackingEdge = (resources.displayMetrics.widthPixels * swipeEdgePercent).toInt()
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
        contentView?.layout(contentOffset, 0, contentOffset + contentView!!.measuredWidth, contentView!!.measuredHeight)
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
        val baseAlpha = (scrimColor and 0xff000000.toInt()).ushr(24)
        val alpha = (baseAlpha * scrimOpacity).toInt()
        val color = alpha shl 24 or (scrimColor and 0xffffff)
        canvas.clipRect(0, 0, child.left, height)
        canvas.drawColor(color)
    }

    fun attachToActivity(activity: Activity) {
        if (parent != null) return
        this.activity = activity
        val a = activity.theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
        val background = a.getResourceId(0, 0)
        a.recycle()

        val decor = activity.window.decorView as ViewGroup
        var decorChild = decor.findViewById<View>(android.R.id.content)
        while (decorChild.parent !== decor) {
            decorChild = decorChild.parent as View
        }
        decorChild.setBackgroundResource(background)
        decor.removeView(decorChild)
        addView(decorChild)
        setContentView(decorChild)
        decor.addView(this)
    }

    fun removeFromActivity(activity: Activity) {
        if (parent == null) return
        val decorChild = getChildAt(0) as ViewGroup
        val decor = activity.window.decorView as ViewGroup
        decor.removeView(this)
        removeView(decorChild)
        decor.addView(decorChild)
    }

    override fun computeScroll() {
        scrimOpacity = 1 - scrollPercent
        if (dragHelper.continueSettling(true)) ViewCompat.postInvalidateOnAnimation(this)
    }

    private inner class ViewDragCallback : ViewDragHelper.Callback() {
        private var isScrollOverValid: Boolean = false

        override fun tryCaptureView(view: View, i: Int): Boolean {
            val ret = dragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT, i)
            if (ret) {
                listeners.forEach { it.get()?.onEdgeTouch() }
                isScrollOverValid = true
            }
            return ret
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return trackingEdge
        }

        override fun getViewVerticalDragRange(child: View?): Int {
            return 0
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            scrollPercent = Math.abs(left.toFloat() / contentView!!.width)
            contentOffset = left
            invalidate()
            if (scrollPercent < scrollThreshold && !isScrollOverValid)
                isScrollOverValid = true

            listeners.forEach { it.get()?.onScroll(scrollPercent, contentOffset) }

            if (scrollPercent >= 1) {
                if (!(activity?.isFinishing ?: true)) {
                    if (scrollPercent >= scrollThreshold && isScrollOverValid) {
                        isScrollOverValid = false
                        listeners.forEach { it.get()?.onScrollToClose() }
                    }
                    activity?.finish()
                }
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val childWidth = releasedChild.width
            val top = 0
            val left = if (xvel > 0 || xvel == 0f && scrollPercent > scrollThreshold)
                childWidth + OVERSCROLL_DISTANCE
            else 0
            dragHelper.settleCapturedViewAt(left, top)
            invalidate()
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return Math.min(child.width, Math.max(left, 0))
        }
    }

    companion object {
        /**
         * Minimum velocity that will be detected as a fling
         */
        private const val MIN_FLING_VELOCITY = 400 // dips per second

        private const val DEFAULT_SCRIM_COLOR = 0x99000000.toInt()

        /**
         * Default threshold of scroll
         */
        private const val DEFAULT_SCROLL_THRESHOLD = 0.3f

        private const val OVERSCROLL_DISTANCE = 10
    }
}
