package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.ViewPropertyAnimator

/**
 * Created by Allan Wang on 2017-06-27.
 */
class SlideUpExitRightAnimator(itemDelayFactor: Float = 0.125f) : BaseSlideAlphaAnimator(itemDelayFactor) {

    override fun addAnimationPrepare(holder: RecyclerView.ViewHolder) {
        with(holder.itemView) {
            translationY = height.toFloat()
            alpha = 0f
        }
    }

    override fun removeAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return super.removeAnimation(holder).apply {
            translationX(holder.itemView.width.toFloat())
        }
    }
}