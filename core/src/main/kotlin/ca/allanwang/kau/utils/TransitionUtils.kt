package ca.allanwang.kau.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.TransitionRes
import androidx.transition.AutoTransition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import android.transition.Transition
import android.view.ViewGroup
import androidx.transition.Transition as SupportTransition

/**
 * Created by Allan Wang on 2017-06-24.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class TransitionEndListener(val onEnd: (transition: Transition) -> Unit) : Transition.TransitionListener {
    override fun onTransitionEnd(transition: Transition) = onEnd(transition)
    override fun onTransitionResume(transition: Transition) {}
    override fun onTransitionPause(transition: Transition) {}
    override fun onTransitionCancel(transition: Transition) {}
    override fun onTransitionStart(transition: Transition) {}
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@KauUtils
fun Transition.addEndListener(onEnd: (transition: Transition) -> Unit) {
    addListener(TransitionEndListener(onEnd))
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SupportTransitionEndListener(val onEnd: (transition: SupportTransition) -> Unit) : SupportTransition.TransitionListener {
    override fun onTransitionEnd(transition: SupportTransition) = onEnd(transition)
    override fun onTransitionResume(transition: SupportTransition) {}
    override fun onTransitionPause(transition: SupportTransition) {}
    override fun onTransitionCancel(transition: SupportTransition) {}
    override fun onTransitionStart(transition: SupportTransition) {}
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@KauUtils
fun SupportTransition.addEndListener(onEnd: (transition: SupportTransition) -> Unit) {
    addListener(SupportTransitionEndListener(onEnd))
}

@KauUtils
fun ViewGroup.transitionAuto(builder: AutoTransition.() -> Unit = {}) {
    if (!buildIsLollipopAndUp) return
    val transition = AutoTransition()
    transition.builder()
    TransitionManager.beginDelayedTransition(this, transition)
}

@KauUtils
fun ViewGroup.transitionDelayed(@TransitionRes id: Int, builder: androidx.transition.Transition.() -> Unit = {}) {
    if (!buildIsLollipopAndUp) return
    val transition = TransitionInflater.from(context).inflateTransition(id)
    transition.builder()
    TransitionManager.beginDelayedTransition(this, transition)
}