package ca.allanwang.kau.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
    private val paint: Paint = Paint()
    private var baseColor = Color.TRANSPARENT
    private val ripples: MutableList<Ripple> = mutableListOf()

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
    }

    /**
     * Drawing the ripples involves having access to the next layer if it exists,
     * and using its values to decide on the current color.
     * If the next layer requests a fade, we will adjust the alpha of our current layer before drawing.
     * Otherwise we will just draw the color as intended
     */
    override fun onDraw(canvas: Canvas) {
        val itr = ripples.listIterator()
        if (!itr.hasNext()) return canvas.drawColor(baseColor)
        var next = itr.next()
        canvas.drawColor(colorToDraw(baseColor, next.fade, next.radius, next.maxRadius))
        var last = false
        while (!last) {
            val current = next
            if (itr.hasNext()) next = itr.next()
            else last = true
            //We may fade any layer except for the last one
            paint.color = colorToDraw(current.color, next.fade && !last, next.radius, next.maxRadius)
            canvas.drawCircle(current.x, current.y, current.radius, paint)
            if (current.radius == current.maxRadius) {
                if (!last) {
                    itr.previous()
                    itr.remove()
                    itr.next()
                } else {
                    itr.remove()
                }
                baseColor = current.color
            }
        }
    }

    /**
     * Given our current color and next layer's radius & max,
     * we will decide on the alpha of our current layer
     */
    internal fun colorToDraw(color: Int, fade: Boolean, current: Float, goal: Float): Int {
        if (!fade || (current / goal <= FADE_PIVOT)) return color
        val factor = (goal - current) / (goal - FADE_PIVOT * goal)
        return color.adjustAlpha(factor)
    }

    fun ripple(color: Int, startX: Float = 0f, startY: Float = 0f, duration: Int = 1000, fade: Boolean = Color.alpha(color) != 255) {
        var x = startX
        var y = startY
        val w = width.toFloat()
        val h = height.toFloat()
        if (x == MIDDLE)
            x = w / 2
        else if (x > w) x = 0f
        if (y == MIDDLE)
            y = h / 2
        else if (y > h) y = 0f
        val maxRadius = Math.hypot(Math.max(x, w - x).toDouble(), Math.max(y, h - y).toDouble()).toFloat()
        val ripple = Ripple(color, x, y, 0f, maxRadius, fade)
        ripples.add(ripple)
        val animator = ValueAnimator.ofFloat(0f, maxRadius)
        animator.duration = duration.toLong()
        animator.addUpdateListener { animation ->
            ripple.radius = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    fun set(color: Int) {
        baseColor = color
        ripples.clear()
        invalidate()
    }

    internal class Ripple(val color: Int,
                          val x: Float,
                          val y: Float,
                          var radius: Float,
                          val maxRadius: Float,
                          val fade: Boolean)

    companion object {
        const val MIDDLE = -1.0f
        const val FADE_PIVOT = 0.5f
    }
}
