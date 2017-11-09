package ca.allanwang.kau.ui.views

import android.animation.ValueAnimator
import android.view.View
import ca.allanwang.kau.utils.*
import java.lang.ref.WeakReference

/**
 * Created by Allan Wang on 2017-08-03.
 *
 * Delegation class for collapsible views
 *
 * Views that implement this MUST call [initCollapsible] before using any of the methods
 * Additionally, you will need to call [getCollapsibleDimension] and use the response for
 * [View.setMeasuredDimension] during [View.onMeasure]
 * (That method is protected so we cannot access it here)
 *
 * With reference to <a href="https://github.com/cachapa/ExpandableLayout">ExpandableLayout</a>
 */
interface CollapsibleView {
    var expansion: Float
    val state: Int
    val expanded: Boolean
    fun initCollapsible(view: View)
    fun resetCollapsibleAnimation()
    fun getCollapsibleDimension(): Pair<Int, Int>
    fun toggleExpansion()
    fun toggleExpansion(animate: Boolean)
    fun expand()
    fun expand(animate: Boolean)
    fun collapse()
    fun collapse(animate: Boolean)
    fun setExpanded(expand: Boolean)
    fun setExpanded(expand: Boolean, animate: Boolean)
}

class CollapsibleViewDelegate : CollapsibleView {

    private lateinit var viewRef: WeakReference<View>
    private inline val view
        get() = viewRef.get()
    private var animator: ValueAnimator? = null

    override var expansion = 0f
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
            view?.goneIf(state == KAU_COLLAPSED)
            view?.requestLayout()
        }

    private var stateHolder = KAU_COLLAPSED
    override val state
        get() = stateHolder
    override val expanded
        get() = stateHolder == KAU_EXPANDED || stateHolder == KAU_EXPANDING

    override fun initCollapsible(view: View) {
        this.viewRef = WeakReference(view)
    }

    override fun resetCollapsibleAnimation() {
        animator?.cancel()
        animator = null
        if (stateHolder == KAU_COLLAPSING) stateHolder = KAU_COLLAPSED
        else if (stateHolder == KAU_EXPANDING) stateHolder = KAU_EXPANDED
    }

    override fun getCollapsibleDimension(): Pair<Int, Int> {
        val v = view ?: return Pair(0, 0)
        val size = v.measuredHeight
        v.goneIf(expansion == 0f && size == 0)
        return Pair(v.measuredWidth, Math.round(size * expansion))
    }

    private fun animateSize(target: Float) {
        resetCollapsibleAnimation()
        animator = ValueAnimator.ofFloat(expansion, target).apply {
            addUpdateListener { expansion = it.animatedValue as Float }
            start()
        }
    }

    override fun toggleExpansion() = toggleExpansion(true)
    override fun toggleExpansion(animate: Boolean) = setExpanded(!expanded, animate)
    override fun expand() = expand(true)
    override fun expand(animate: Boolean) = setExpanded(true, animate)
    override fun collapse() = collapse(true)
    override fun collapse(animate: Boolean) = setExpanded(false, animate)
    override fun setExpanded(expand: Boolean) = setExpanded(expand, true)
    override fun setExpanded(expand: Boolean, animate: Boolean) {
        if (expand == expanded) return //state already matches
        val target = if (expand) 1f else 0f
        if (animate) animateSize(target) else expansion = target
    }

}