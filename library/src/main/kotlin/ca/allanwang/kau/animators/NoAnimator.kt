package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.ViewPropertyAnimator

/**
 * Created by Allan Wang on 2017-06-27.
 *
 * Truly have no animation
 */
class NoAnimator : DefaultAnimator() {
    override fun addAnimationPrepare(holder: RecyclerView.ViewHolder) {}

    override fun addAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator = holder.itemView.animate().apply { duration = 0 }

    override fun addAnimationCleanup(holder: RecyclerView.ViewHolder) {}

    override fun changeOldAnimation(holder: RecyclerView.ViewHolder, changeInfo: ChangeInfo): ViewPropertyAnimator = holder.itemView.animate().apply { duration = 0 }

    override fun changeNewAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator = holder.itemView.animate().apply { duration = 0 }

    override fun changeAnimationCleanup(holder: RecyclerView.ViewHolder) {}

    override fun changeAnimation(oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder?, fromX: Int, fromY: Int, toX: Int, toY: Int) {}

    override fun getAddDelay(remove: Long, move: Long, change: Long): Long = 0

    override fun getAddDuration(): Long = 0

    override fun getMoveDuration(): Long = 0

    override fun getRemoveDuration(): Long = 0

    override fun getChangeDuration(): Long = 0

    override fun getRemoveDelay(remove: Long, move: Long, change: Long): Long = 0

    override fun removeAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator = holder.itemView.animate().apply { duration = 0 }

    override fun removeAnimationCleanup(holder: RecyclerView.ViewHolder) {}
}