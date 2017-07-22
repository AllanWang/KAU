package ca.allanwang.kau.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.support.annotation.StringRes
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView

/**
 * Created by Allan Wang on 2017-06-01.
 *
 * Animation extension functions for Views
 */

@SuppressLint("NewApi")
@KauUtils fun View.circularReveal(x: Int = 0, y: Int = 0, offset: Long = 0L, radius: Float = -1.0f, duration: Long = 500L, onStart: (() -> Unit)? = null, onFinish: (() -> Unit)? = null) {
    if (!isAttachedToWindow) {
        onStart?.invoke()
        visible()
        onFinish?.invoke()
        return
    }
    if (!buildIsLollipopAndUp) return fadeIn(offset, duration, onStart, onFinish)

    val r = if (radius >= 0) radius
    else Math.max(Math.hypot(x.toDouble(), y.toDouble()), Math.hypot((width - x.toDouble()), (height - y.toDouble()))).toFloat()

    val anim = ViewAnimationUtils.createCircularReveal(this, x, y, 0f, r).setDuration(duration)
    anim.startDelay = offset
    anim.addListener(object : AnimatorListenerAdapter() {
        override @KauUtils fun onAnimationStart(animation: Animator?) {
            visible()
            onStart?.invoke()
        }

        override @KauUtils fun onAnimationEnd(animation: Animator?) = onFinish?.invoke() ?: Unit
        override @KauUtils fun onAnimationCancel(animation: Animator?) = onFinish?.invoke() ?: Unit
    })
    anim.start()
}

@SuppressLint("NewApi")
@KauUtils fun View.circularHide(x: Int = 0, y: Int = 0, offset: Long = 0L, radius: Float = -1.0f, duration: Long = 500L, onStart: (() -> Unit)? = null, onFinish: (() -> Unit)? = null) {
    if (!isAttachedToWindow) {
        onStart?.invoke()
        invisible()
        onFinish?.invoke()
        return
    }
    if (!buildIsLollipopAndUp) return fadeOut(offset, duration, onStart, onFinish)

    val r = if (radius >= 0) radius
    else Math.max(Math.hypot(x.toDouble(), y.toDouble()), Math.hypot((width - x.toDouble()), (height - y.toDouble()))).toFloat()

    val anim = ViewAnimationUtils.createCircularReveal(this, x, y, r, 0f).setDuration(duration)
    anim.startDelay = offset
    anim.addListener(object : AnimatorListenerAdapter() {
        override @KauUtils fun onAnimationStart(animation: Animator?) = onStart?.invoke() ?: Unit

        override @KauUtils fun onAnimationEnd(animation: Animator?) {
            invisible()
            onFinish?.invoke() ?: Unit
        }

        override @KauUtils fun onAnimationCancel(animation: Animator?) = onFinish?.invoke() ?: Unit
    })
    anim.start()
}

@KauUtils fun View.fadeIn(offset: Long = 0L, duration: Long = 200L, onStart: (() -> Unit)? = null, onFinish: (() -> Unit)? = null) {
    if (!isAttachedToWindow) {
        onStart?.invoke()
        visible()
        onFinish?.invoke()
        return
    }
    val anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
    anim.startOffset = offset
    anim.duration = duration
    anim.setAnimationListener(object : Animation.AnimationListener {
        override @KauUtils fun onAnimationRepeat(animation: Animation?) {}
        override @KauUtils fun onAnimationEnd(animation: Animation?) = onFinish?.invoke() ?: Unit
        override @KauUtils fun onAnimationStart(animation: Animation?) {
            visible()
            onStart?.invoke()
        }
    })
    startAnimation(anim)
}

@KauUtils fun View.fadeOut(offset: Long = 0L, duration: Long = 200L, onStart: (() -> Unit)? = null, onFinish: (() -> Unit)? = null) {
    if (!isAttachedToWindow) {
        onStart?.invoke()
        invisible()
        onFinish?.invoke()
        return
    }
    val anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
    anim.startOffset = offset
    anim.duration = duration
    anim.setAnimationListener(object : Animation.AnimationListener {
        override @KauUtils fun onAnimationRepeat(animation: Animation?) {}
        override @KauUtils fun onAnimationEnd(animation: Animation?) {
            invisible()
            onFinish?.invoke()
        }

        override @KauUtils fun onAnimationStart(animation: Animation?) {
            onStart?.invoke()
        }
    })
    startAnimation(anim)
}

@KauUtils fun TextView.setTextWithFade(text: String, duration: Long = 200, onFinish: (() -> Unit)? = null) {
    fadeOut(duration = duration, onFinish = {
        setText(text)
        fadeIn(duration = duration, onFinish = onFinish)
    })
}

@KauUtils fun TextView.setTextWithFade(@StringRes textId: Int, duration: Long = 200, onFinish: (() -> Unit)? = null) = setTextWithFade(context.getString(textId), duration, onFinish)