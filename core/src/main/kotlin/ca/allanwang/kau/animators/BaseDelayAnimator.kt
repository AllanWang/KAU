package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.ViewPropertyAnimator

/**
 * Created by Allan Wang on 2017-06-27.
 *
 * Base for delayed animators
 * item delay factor by default can be 0.125f
 */
abstract class BaseDelayAnimator(val itemDelayFactor: Float) : DefaultAnimator() {

    override abstract fun addAnimationPrepare(holder: RecyclerView.ViewHolder)

    override fun addAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            startDelay = Math.max(0L, (holder.adapterPosition * addDuration * itemDelayFactor).toLong())
            duration = this@BaseDelayAnimator.addDuration
            interpolator = this@BaseDelayAnimator.interpolator
        }
    }


    override abstract fun addAnimationCleanup(holder: RecyclerView.ViewHolder)

    override fun getAddDelay(remove: Long, move: Long, change: Long): Long = 0

    override fun getRemoveDelay(remove: Long, move: Long, change: Long): Long = 0

    /**
     * Partial removal animation
     * As of now, all it does it change the alpha
     * To have it slide, add onto it in a sub class
     */
    override fun removeAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            duration = this@BaseDelayAnimator.removeDuration
            startDelay = Math.max(0L, (holder.adapterPosition * removeDuration * itemDelayFactor).toLong())
            interpolator = this@BaseDelayAnimator.interpolator
        }
    }

    override abstract fun removeAnimationCleanup(holder: RecyclerView.ViewHolder)
}