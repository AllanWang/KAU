package ca.allanwang.kau.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Wrap the color into a state and tint the drawable
 */
fun Drawable.tint(@ColorInt color: Int): Drawable = tint(ColorStateList.valueOf(color))

/**
 * Tint the drawable with a given color state list
 */
fun Drawable.tint(state: ColorStateList): Drawable {
    val drawable = DrawableCompat.wrap(mutate())
    DrawableCompat.setTintList(drawable, state)
    return drawable
}