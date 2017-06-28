package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.ViewPropertyAnimator

/**
 * Created by Allan Wang on 2017-06-27.
 */
class SlideUpAlphaAnimator : DefaultAnimator() {
    override fun addAnimationPrepare(holder: RecyclerView.ViewHolder) {
        with(holder.itemView) {
            translationY = height.toFloat()
            alpha = 0f
        }
    }

    override fun addAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            translationY(0f)
            alpha(0f)
            duration = this@SlideUpAlphaAnimator.addDuration
            interpolator = this@SlideUpAlphaAnimator.interpolator
        }
    }

    public override fun addAnimationCleanup(holder: RecyclerView.ViewHolder) {
        with(holder.itemView) {
            translationY = 0f
            alpha = 1f
        }
    }

    override fun getAddDelay(remove: Long, move: Long, change: Long): Long = 0

    override fun getRemoveDelay(remove: Long, move: Long, change: Long): Long = 0

    override fun removeAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            duration = this@SlideUpAlphaAnimator.removeDuration
            alpha(0f)
            translationY(holder.itemView.height.toFloat())
            interpolator = this@SlideUpAlphaAnimator.interpolator
        }
    }

    override fun removeAnimationCleanup(holder: RecyclerView.ViewHolder) {
        with(holder.itemView) {
            translationY = 0f
            alpha = 1f
        }
    }
}