package ca.allanwang.kau.animators

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewPropertyAnimator

/**
 * Created by Allan Wang on 2017-07-11.
 */
class FadeScaleAnimatorAdd(val scaleFactor: Float = 1.0f, override var itemDelayFactor: Float = 0.125f) : KauAnimatorAdd {

    override fun animationPrepare(holder: RecyclerView.ViewHolder): View.() -> Unit = {
        scaleX = scaleFactor
        scaleY = scaleFactor
        alpha = 0f
    }

    override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {
        scaleX(1f)
        scaleY(1f)
        alpha(1f)
    }

    override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
        scaleX = 1f
        scaleY = 1f
        alpha = 1f
    }

    override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L

}

class FadeScaleAnimatorRemove(val scaleFactor: Float = 1.0f, override var itemDelayFactor: Float = 0.125f) : KauAnimatorRemove {

    override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {
        scaleX(scaleFactor)
        scaleY(scaleFactor)
        alpha(0f)
    }

    override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
        scaleX = 1f
        scaleY = 1f
        alpha = 1f
    }

    override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}

class FadeAnimatorChange : KauAnimatorChange {

    override fun changeOldAnimation(holder: RecyclerView.ViewHolder, changeInfo: BaseItemAnimator.ChangeInfo): ViewPropertyAnimator.() -> Unit = { alpha(0f) }

    override fun changeNewAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = { alpha(1f) }

    override fun changeAnimationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = { alpha = 1f }

}