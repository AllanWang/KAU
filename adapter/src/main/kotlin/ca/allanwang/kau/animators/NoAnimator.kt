package ca.allanwang.kau.animators

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Allan Wang on 2017-08-02.
 */
class NoAnimatorAdd(override var itemDelayFactor: Float = 0f) : KauAnimatorAdd {

    override fun animationPrepare(holder: RecyclerView.ViewHolder): View.() -> Unit = {}

    override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {}

    override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = { }

    override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}

class NoAnimatorRemove(override var itemDelayFactor: Float = 0f) : KauAnimatorRemove {

    override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = { }

    override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {}

    override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}

class NoAnimatorChange : KauAnimatorChange {

    override fun changeOldAnimation(
        holder: RecyclerView.ViewHolder,
        changeInfo: BaseItemAnimator.ChangeInfo
    ): ViewPropertyAnimator.() -> Unit = { }

    override fun changeNewAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = { }

    override fun changeAnimationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = { alpha = 1f }
}