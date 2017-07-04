package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.ViewPropertyAnimator

/**
 * Created by Allan Wang on 2017-06-29.
 */
open class FadeScaleAnimator(val scaleFactor: Float = 0.7f, itemDelayFactor: Float = 0.125f) : BaseDelayAnimator(itemDelayFactor) {

    override fun addAnimationPrepare(holder: RecyclerView.ViewHolder) {
        with(holder.itemView) {
            scaleX = scaleFactor
            scaleY = scaleFactor
            alpha = 0f
        }
    }

    override final fun addAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return super.addAnimation(holder).apply {
            scaleX(1f)
            scaleY(1f)
            alpha(1f)
        }
    }


    final override fun addAnimationCleanup(holder: RecyclerView.ViewHolder) {
        with(holder.itemView) {
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
        }
    }

    override fun removeAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return super.removeAnimation(holder).apply {
            scaleX(scaleFactor)
            scaleY(scaleFactor)
            alpha(0f)
        }
    }

    override final fun removeAnimationCleanup(holder: RecyclerView.ViewHolder) {
        with(holder.itemView) {
            translationY = 0f
            translationX = 0f
            alpha = 1f
        }
    }
}