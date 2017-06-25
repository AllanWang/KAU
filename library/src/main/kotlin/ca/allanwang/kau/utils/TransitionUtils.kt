package ca.allanwang.kau.utils

import android.support.transition.Transition
import android.support.transition.TransitionSet

/**
 * Created by Allan Wang on 2017-06-24.
 */
class TransitionEndListener(val onEnd: (transition: Transition) -> Unit) : Transition.TransitionListener {
    override fun onTransitionEnd(transition: Transition) = onEnd(transition)
    override fun onTransitionResume(transition: Transition) {}
    override fun onTransitionPause(transition: Transition) {}
    override fun onTransitionCancel(transition: Transition) {}
    override fun onTransitionStart(transition: Transition) {}
}

fun TransitionSet.addEndListener(onEnd: (transition: Transition) -> Unit) {
    addListener(TransitionEndListener(onEnd))
}