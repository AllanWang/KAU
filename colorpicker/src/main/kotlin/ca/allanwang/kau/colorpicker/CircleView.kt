package ca.allanwang.kau.colorpicker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.FloatRange
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import ca.allanwang.kau.utils.*

/**
 * Created by Allan Wang on 2017-06-10.
 *
 * An extension of MaterialDialog's CircleView with animation selection
 * [https://github.com/afollestad/material-dialogs/blob/master/commons/src/main/java/com/afollestad/materialdialogs/color/CircleView.java]
 */
class CircleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private val borderWidthMicro: Float = context.getDip(1f)
    private val borderWidthSmall: Float = context.getDip(3f)
    private val borderWidthLarge: Float = context.getDip(5f)
    private var whiteOuterBound: Float = borderWidthLarge

    private val outerPaint: Paint = Paint().apply { isAntiAlias = true }
    private val whitePaint: Paint = Paint().apply { isAntiAlias = true; color = Color.WHITE }
    private val innerPaint: Paint = Paint().apply { isAntiAlias = true }
    private var selected: Boolean = false
    var withBorder: Boolean = false
        get() = field
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    init {
        update(Color.DKGRAY)
        setWillNotDraw(false)
    }

    private fun update(@ColorInt color: Int) {
        innerPaint.color = color
        outerPaint.color = shiftColorDown(color)

        val selector = createSelector(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val states = arrayOf(intArrayOf(android.R.attr.state_pressed))
            val colors = intArrayOf(shiftColorUp(color))
            val rippleColors = ColorStateList(states, colors)
            foreground = RippleDrawable(rippleColors, selector, null)
        } else {
            foreground = selector
        }
    }

    override fun setBackgroundColor(@ColorInt color: Int) {
        update(color)
        requestLayout()
        invalidate()
    }

    override fun setBackgroundResource(@ColorRes color: Int) {
        setBackgroundColorRes(color)
    }


    @Deprecated("")
    override fun setBackground(background: Drawable) {
        throw IllegalStateException("Cannot use setBackground() on CircleView.")
    }


    @Deprecated("")
    override fun setBackgroundDrawable(background: Drawable) {
        throw IllegalStateException("Cannot use setBackgroundDrawable() on CircleView.")
    }


    @Deprecated("")
    override fun setActivated(activated: Boolean) {
        throw IllegalStateException("Cannot use setActivated() on CircleView.")
    }

    override fun setSelected(selected: Boolean) {
        this.selected = selected
        whiteOuterBound = borderWidthLarge
        invalidate()
    }

    fun animateSelected(selected: Boolean) {
        if (this.selected == selected) return
        this.selected = true // We need to draw the other bands
        val range = if (selected) Pair(-borderWidthSmall, borderWidthLarge) else Pair(borderWidthLarge, -borderWidthSmall)
        val animator = ValueAnimator.ofFloat(range.first, range.second)
        with(animator) {
            reverse()
            duration = 150L
            addUpdateListener { animation ->
                whiteOuterBound = animation.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    this@CircleView.selected = selected
                }

                override fun onAnimationCancel(animation: Animator) {
                    this@CircleView.selected = selected
                }
            })
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerWidth = (measuredWidth / 2).toFloat()
        val centerHeight = (measuredHeight / 2).toFloat()
        if (withBorder) canvas.drawCircle(centerWidth, centerHeight, centerWidth, whitePaint)
        if (selected) {
            val whiteRadius = centerWidth - whiteOuterBound
            val innerRadius = whiteRadius - borderWidthSmall
            if (whiteRadius >= centerWidth) {
                canvas.drawCircle(centerWidth, centerHeight, centerWidth, whitePaint)
            } else {
                canvas.drawCircle(centerWidth, centerHeight, if (withBorder) centerWidth - borderWidthMicro else centerWidth, outerPaint)
                canvas.drawCircle(centerWidth, centerHeight, whiteRadius, whitePaint)
            }
            canvas.drawCircle(centerWidth, centerHeight, innerRadius, innerPaint)
        } else {
            canvas.drawCircle(centerWidth, centerHeight, if (withBorder) centerWidth - borderWidthMicro else centerWidth, innerPaint)
        }
    }

    private fun createSelector(color: Int): Drawable {
        val darkerCircle = ShapeDrawable(OvalShape())
        darkerCircle.paint.color = translucentColor(shiftColorUp(color))
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), darkerCircle)
        return stateListDrawable
    }

    fun showHint(color: Int) {
        val screenPos = IntArray(2)
        val displayFrame = Rect()
        getLocationOnScreen(screenPos)
        getWindowVisibleDisplayFrame(displayFrame)
        val context = context
        val width = width
        val height = height
        val midy = screenPos[1] + height / 2
        var referenceX = screenPos[0] + width / 2
        if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            val screenWidth = context.resources.displayMetrics.widthPixels
            referenceX = screenWidth - referenceX // mirror
        }
        val cheatSheet = Toast
                .makeText(context, String.format("#%06X", 0xFFFFFF and color), Toast.LENGTH_SHORT)
        if (midy < displayFrame.height()) {
            // Show along the top; follow action buttons
            cheatSheet.setGravity(Gravity.TOP or GravityCompat.END, referenceX,
                    screenPos[1] + height - displayFrame.top)
        } else {
            // Show along the bottom center
            cheatSheet.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, height)
        }
        cheatSheet.show()
    }

    companion object {

        @ColorInt
        @JvmStatic
        private fun translucentColor(color: Int): Int {
            val factor = 0.7f
            val alpha = Math.round(Color.alpha(color) * factor)
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return Color.argb(alpha, red, green, blue)
        }

        @ColorInt
        @JvmStatic
        fun shiftColor(@ColorInt color: Int,
                       @FloatRange(from = 0.0, to = 2.0) by: Float): Int {
            if (by == 1f) return color
            val hsv = color.toHSV()
            hsv[2] *= by // value component
            return hsv.toColor()
        }

        @ColorInt
        @JvmStatic
        fun shiftColorDown(@ColorInt color: Int): Int = shiftColor(color, 0.9f)

        @ColorInt
        @JvmStatic
        fun shiftColorUp(@ColorInt color: Int): Int = shiftColor(color, 1.1f)
    }
}