package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.ViewPropertyAnimator
import ca.allanwang.kau.utils.KAU_BOTTOM
import ca.allanwang.kau.utils.KAU_RIGHT

/**
 * Created by Allan Wang on 2017-06-27.
 */
class KauAnimator(
        val addAnimator: KauAnimatorAdd = SlideAnimatorAdd(KAU_BOTTOM),
        val removeAnimator: KauAnimatorRemove = SlideAnimatorRemove(KAU_RIGHT),
        val changeAnimator: KauAnimatorChange = FadeAnimatorChange()
) : BaseItemAnimator() {

    fun startDelay(holder: RecyclerView.ViewHolder, duration: Long, factor: Float)
            = Math.max(0L, (holder.adapterPosition * duration * factor).toLong())

    override fun removeAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            startDelay = startDelay(holder, removeDuration, removeAnimator.itemDelayFactor)
            duration = removeDuration
            interpolator = this@KauAnimator.interpolator
            removeAnimator.animation(holder)()
        }
    }

    override fun removeAnimationCleanup(holder: RecyclerView.ViewHolder) {
        holder.itemView.apply { removeAnimator.animationCleanup(holder)() }
    }

    override fun getRemoveDelay(remove: Long, move: Long, change: Long): Long
            = removeAnimator.getDelay(remove, move, change)

    override fun addAnimationPrepare(holder: RecyclerView.ViewHolder) {
        holder.itemView.apply { addAnimator.animationPrepare(holder)() }
    }

    override fun addAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            startDelay = startDelay(holder, addDuration, addAnimator.itemDelayFactor)
            duration = addDuration
            interpolator = this@KauAnimator.interpolator
            addAnimator.animation(holder)()
        }
    }

    override fun addAnimationCleanup(holder: RecyclerView.ViewHolder) {
        holder.itemView.apply { addAnimator.animationCleanup(holder)() }
    }

    override fun getAddDelay(remove: Long, move: Long, change: Long): Long
            = addAnimator.getDelay(remove, move, change)

    override fun changeOldAnimation(holder: RecyclerView.ViewHolder, changeInfo: ChangeInfo): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            duration = changeDuration
            interpolator = this@KauAnimator.interpolator
            changeAnimator.changeOldAnimation(holder, changeInfo)()
        }
    }

    override fun changeNewAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            duration = changeDuration
            interpolator = this@KauAnimator.interpolator
            changeAnimator.changeNewAnimation(holder)()
        }
    }

    override fun changeAnimationCleanup(holder: RecyclerView.ViewHolder) {
        holder.itemView.apply { changeAnimator.changeAnimationCleanup(holder)() }
    }

}