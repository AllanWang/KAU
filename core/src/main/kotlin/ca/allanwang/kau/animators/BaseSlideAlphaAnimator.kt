package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.ViewPropertyAnimator

/**
 * Created by Allan Wang on 2017-06-27.
 *
 * Base for sliding animators
 * item delay factor by default can be 0.125f
 */
abstract class BaseSlideAlphaAnimator(itemDelayFactor: Float) : BaseDelayAnimator(itemDelayFactor) {

    override fun addAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return super.addAnimation(holder).apply {
            translationY(0f)
            translationX(0f)
            alpha(1f)
        }
    }

    final override fun addAnimationCleanup(holder: RecyclerView.ViewHolder) {
        with(holder.itemView) {
            translationY = 0f
            translationX = 0f
            alpha = 1f
        }
    }

    override fun getAddDelay(remove: Long, move: Long, change: Long): Long = 0

    override fun getRemoveDelay(remove: Long, move: Long, change: Long): Long = 0

    /**
     * Partial removal animation
     * As of now, all it does it change the alpha
     * To have it slide, add onto it in a sub class
     */
    override fun removeAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return super.addAnimation(holder).apply {
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