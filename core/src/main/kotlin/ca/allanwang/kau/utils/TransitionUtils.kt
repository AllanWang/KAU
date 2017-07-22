package ca.allanwang.kau.utils

import android.support.transition.Transition as SupportTransition
import android.transition.Transition

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

@KauUtils fun Transition.addEndListener(onEnd: (transition: Transition) -> Unit) {
    addListener(TransitionEndListener(onEnd))
}

class SupportTransitionEndListener(val onEnd: (transition: SupportTransition) -> Unit) : SupportTransition.TransitionListener {
    override fun onTransitionEnd(transition: SupportTransition) = onEnd(transition)
    override fun onTransitionResume(transition: SupportTransition) {}
    override fun onTransitionPause(transition: SupportTransition) {}
    override fun onTransitionCancel(transition: SupportTransition) {}
    override fun onTransitionStart(transition: SupportTransition) {}
}

@KauUtils fun SupportTransition.addEndListener(onEnd: (transition: SupportTransition) -> Unit) {
    addListener(SupportTransitionEndListener(onEnd))
}