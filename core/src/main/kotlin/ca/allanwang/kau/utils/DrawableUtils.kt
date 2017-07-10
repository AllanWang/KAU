package ca.allanwang.kau.utils

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat

fun Drawable.tintWithColor(@ColorInt color:Int):Drawable? {
    val nDrawable = DrawableCompat.wrap(mutate())
    DrawableCompat.setTintMode(nDrawable, PorterDuff.Mode.SRC_IN)
    DrawableCompat.setTint(nDrawable, color)
    return nDrawable
}

fun Drawable.tintWithColor(sl:ColorStateList):Drawable? {
    val nDrawable = DrawableCompat.wrap(mutate())
    DrawableCompat.setTintList(nDrawable, sl)
    return nDrawable
}