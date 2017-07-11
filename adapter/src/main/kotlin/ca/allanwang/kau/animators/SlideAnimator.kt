package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewPropertyAnimator
import ca.allanwang.kau.utils.KAU_BOTTOM
import ca.allanwang.kau.utils.KAU_LEFT
import ca.allanwang.kau.utils.KAU_RIGHT
import ca.allanwang.kau.utils.KAU_TOP

/**
 * Created by Allan Wang on 2017-07-11.
 */
class SlideAnimatorAdd(val fromEdge: Int, override var itemDelayFactor: Float = 0.125f) : KauAnimatorAdd {

    override fun animationPrepare(holder: RecyclerView.ViewHolder): View.() -> Unit = {
        when (fromEdge) {
            KAU_TOP -> translationY = -height.toFloat()
            KAU_LEFT -> translationX = -width.toFloat()
            KAU_BOTTOM -> translationY = height.toFloat()
            KAU_RIGHT -> translationX = width.toFloat()
            else -> throw KauAnimatorException("Invalid edge flag used in Slide Animator; use one of KAU_*")
        }
        alpha = 0f
    }

    override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {
        translationY(0f)
        translationX(0f)
        alpha(1f)
    }

    override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
        translationY = 0f
        translationX = 0f
        alpha = 1f
    }

    override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L

}

class SlideAnimatorRemove(val fromEdge: Int, override var itemDelayFactor: Float = 0.125f) : KauAnimatorRemove {
    override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {
        with(holder.itemView) {
            when (fromEdge) {
                KAU_TOP -> translationY(-height.toFloat())
                KAU_LEFT -> translationX(-width.toFloat())
                KAU_BOTTOM -> translationY(height.toFloat())
                KAU_RIGHT -> translationX(width.toFloat())
                else -> throw KauAnimatorException("Invalid edge flag used in Slide Animator; use one of KAU_*")
            }
        }
        alpha(0f)
    }

    override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
        translationY = 0f
        translationX = 0f
        alpha = 1f
    }

    override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}