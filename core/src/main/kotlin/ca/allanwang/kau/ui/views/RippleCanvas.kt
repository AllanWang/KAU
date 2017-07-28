package ca.allanwang.kau.ui.views

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import ca.allanwang.kau.utils.adjustAlpha

/**
 * Created by Allan Wang on 2016-11-17.
 *
 *
 * Canvas drawn ripples that keep the previous color
 * Extends to view dimensions
 * Supports multiple ripples from varying locations
 */
class RippleCanvas @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val eraser: Paint = Paint().apply {
        style = Paint.Style.FILL
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private var baseColor = Color.TRANSPARENT
    private val ripples: MutableList<Ripple> = mutableListOf()

    /**
     * Draw ripples one at a time in the order given
     * To support transparent ripples, we simply erase the overlapping base before adding a new circle
     */
    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(baseColor)
        val itr = ripples.iterator()
        while (itr.hasNext()) {
            val r = itr.next()
            paint.color = r.color
            canvas.drawCircle(r.x, r.y, r.radius, eraser)
            canvas.drawCircle(r.x, r.y, r.radius, paint)
            if (r.radius == r.maxRadius) {
                itr.remove()
                baseColor = r.color
            }
        }
    }

    /**
     * Creates a ripple effect from the given starting values
     * [fade] will gradually transition previous ripples to a transparent color so the resulting background is what we want
     * this is typically only necessary if the ripple color has transparency
     */
    fun ripple(color: Int, startX: Float = 0f, startY: Float = 0f, duration: Long = 600L, fade: Boolean = Color.alpha(color) != 255) {
        val w = width.toFloat()
        val h = height.toFloat()
        val x = when (startX) {
            MIDDLE -> w / 2
            END -> w
            else -> startX
        }
        val y = when (startY) {
            MIDDLE -> h / 2
            END -> h
            else -> startY
        }
        val maxRadius = Math.hypot(Math.max(x, w - x).toDouble(), Math.max(y, h - y).toDouble()).toFloat()
        val ripple = Ripple(color, x, y, 0f, maxRadius, fade)
        ripples.add(ripple)
        val animator = ValueAnimator.ofFloat(0f, maxRadius)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            ripple.radius = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    /**
     * Sets a color directly; clears ripple queue if it exists
     */
    fun set(color: Int) {
        baseColor = color
        ripples.clear()
        invalidate()
    }

    /**
     * Sets a color directly but with a transition
     */
    fun fade(color: Int, duration: Long = 300L) {
        ripples.clear()
        val animator = ValueAnimator.ofObject(ArgbEvaluator(), baseColor, color)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            baseColor = animation.animatedValue as Int
            invalidate()
        }
        animator.start()
    }

    internal class Ripple(val color: Int,
                          val x: Float,
                          val y: Float,
                          var radius: Float,
                          val maxRadius: Float,
                          val fade: Boolean)

    companion object {
        const val MIDDLE = -1.0f
        const val END = -2.0f
    }
}
