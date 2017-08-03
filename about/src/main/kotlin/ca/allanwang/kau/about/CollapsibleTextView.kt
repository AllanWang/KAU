package ca.allanwang.kau.about

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.*

/**
 * Created by Allan Wang on 2017-08-02.
 *
 * With reference to <a href="https://github.com/cachapa/ExpandableLayout">ExpandableLayout</a>
 */
class CollapsibleTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {
    private var animator: ValueAnimator? = null

    var expansion = 0f
        set(value) {
            if (value == field) return
            var v = value
            if (v > 1) v = 1f
            else if (v < 0) v = 0f
            stateHolder =
                    if (v == 0f) KAU_COLLAPSED
                    else if (v == 1f) KAU_EXPANDED
                    else if (v - field < 0) KAU_COLLAPSING
                    else KAU_EXPANDING
            field = v
            goneIf(state == KAU_COLLAPSED)
            requestLayout()
        }

    private var stateHolder = KAU_COLLAPSED
    val state
        get() = stateHolder
    val expanded
        get() = stateHolder == KAU_EXPANDED || stateHolder == KAU_EXPANDING

    override fun onConfigurationChanged(newConfig: Configuration?) {
        clearAnimation()
        super.onConfigurationChanged(newConfig)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = measuredHeight
        goneIf(expansion == 0f && size == 0)
        KL.d("Measure $isVisible $expansion $size")
        setMeasuredDimension(measuredWidth, Math.round(size * expansion))
    }

    override fun clearAnimation() {
        animator?.cancel()
        animator = null
        if (stateHolder == KAU_COLLAPSING) stateHolder = KAU_COLLAPSED
        else if (stateHolder == KAU_EXPANDING) stateHolder = KAU_EXPANDED
        super.clearAnimation()
    }

    private fun animateSize(target: Float) {
        clearAnimation()
        animator = ValueAnimator.ofFloat(expansion, target).apply {
            addUpdateListener { KL.d("Expand ${expansion}"); expansion = it.animatedValue as Float }
            start()
        }

    }

    fun toggleExpansion(animate: Boolean = true) = setExpanded(!expanded, animate)

    fun expand(animate: Boolean = true) = setExpanded(true, animate)

    fun collapse(animate: Boolean = true) = setExpanded(false, animate)

    fun setExpanded(expand: Boolean, animate: Boolean = true) {
        if (expand == expanded) return //state already matches
        val target = if (expand) 1f else 0f
        KL.d("Expand $expand $animate")
        if (animate) animateSize(target) else expansion = target
    }
}