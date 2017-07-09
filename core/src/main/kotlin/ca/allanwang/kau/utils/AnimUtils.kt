package ca.allanwang.kau.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.annotation.StringRes
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

/**
 * Created by Allan Wang on 2017-06-01.
 *
 * Animation extension @KauUtils functions for Views
 */
@KauUtils fun View.rootCircularReveal(x: Int = 0, y: Int = 0, duration: Long = 500L, onStart: (() -> Unit)? = null, onFinish: (() -> Unit)? = null) {
    this.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override @KauUtils fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                                              oldRight: Int, oldBottom: Int) {
            v.removeOnLayoutChangeListener(this)
            var x2 = x
            var y2 = y
            if (x2 > right) x2 = 0
            if (y2 > bottom) y2 = 0
            val radius = Math.hypot(Math.max(x2, right - x2).toDouble(), Math.max(y2, bottom - y2).toDouble()).toInt()
            val reveal = ViewAnimationUtils.createCircularReveal(v, x2, y2, 0f, radius.toFloat())
            reveal.interpolator = DecelerateInterpolator(1f)
            reveal.duration = duration
            reveal.addListener(object : AnimatorListenerAdapter() {
                override @KauUtils fun onAnimationStart(animation: Animator?) {
                    visible()
                    onStart?.invoke()
                }

                override @KauUtils fun onAnimationEnd(animation: Animator?) = onFinish?.invoke() ?: Unit
                override @KauUtils fun onAnimationCancel(animation: Animator?) = onFinish?.invoke() ?: Unit
            })
            reveal.start()
        }
    })
}

@KauUtils fun View.circularReveal(x: Int = 0, y: Int = 0, offset: Long = 0L, radius: Float = -1.0f, duration: Long = 500L, onStart: (() -> Unit)? = null, onFinish: (() -> Unit)? = null) {
    if (!isAttachedToWindow) {
        onStart?.invoke()
        visible()
        onFinish?.invoke()
        return
    }
    var r = radius
    if (r < 0.0f) {
        r = Math.max(Math.hypot(x.toDouble(), y.toDouble()), Math.hypot((width - x.toDouble()), (height - y.toDouble()))).toFloat()
    }
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

@KauUtils fun View.circularHide(x: Int = 0, y: Int = 0, offset: Long = 0L, radius: Float = -1.0f, duration: Long = 500L, onStart: (() -> Unit)? = null, onFinish: (() -> Unit)? = null) {
    if (!isAttachedToWindow) {
        onStart?.invoke()
        invisible()
        onFinish?.invoke()
        return
    }
    var r = radius
    if (r < 0.0f) {
        r = Math.max(Math.hypot(x.toDouble(), y.toDouble()), Math.hypot((width - x.toDouble()), (height - y.toDouble()))).toFloat()
    }
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
    if (isAttachedToWindow) {
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