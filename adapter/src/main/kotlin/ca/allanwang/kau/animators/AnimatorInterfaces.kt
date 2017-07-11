package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewPropertyAnimator

/**
 * Created by Allan Wang on 2017-07-11.
 */
class KauAnimatorException(message: String) : RuntimeException(message)

interface KauAnimatorAdd {
    fun animationPrepare(holder: RecyclerView.ViewHolder): View.() -> Unit
    fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit
    fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit
    fun getDelay(remove: Long, move: Long, change: Long): Long
    var itemDelayFactor: Float
}

interface KauAnimatorRemove {
    fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit
    fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit
    fun getDelay(remove: Long, move: Long, change: Long): Long
    var itemDelayFactor: Float
}

interface KauAnimatorChange {
    fun changeOldAnimation(holder: RecyclerView.ViewHolder, changeInfo: BaseItemAnimator.ChangeInfo): ViewPropertyAnimator.() -> Unit
    fun changeNewAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit
    fun changeAnimationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit
}
