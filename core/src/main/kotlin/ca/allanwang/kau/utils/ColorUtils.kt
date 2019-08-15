/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import com.afollestad.materialdialogs.R
import java.util.Random

/**
 * Created by Allan Wang on 2017-06-08.
 */

/**
 * Generates a random opaque color
 * Note that this is mainly for testing
 * Should you require this method often, consider
 * rewriting the method and storing the [Random] instance
 * rather than generating one each time
 */
inline val rndColor: Int
    get() {
        val rnd = Random()
        return Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

inline val Int.isColorDark: Boolean
    get() = isColorDark(0.5f)

fun Int.isColorDark(minDarkness: Float): Boolean =
    ((0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255.0) < minDarkness

fun Int.toHexString(withAlpha: Boolean = false, withHexPrefix: Boolean = true): String {
    val hex = if (withAlpha) String.format("#%08X", this)
    else String.format("#%06X", 0xFFFFFF and this)
    return if (withHexPrefix) hex else hex.substring(1)
}

fun Int.toRgbaString(): String =
    "rgba(${Color.red(this)}, ${Color.green(this)}, ${Color.blue(this)}, ${(Color.alpha(this) / 255f).round(3)})"

fun Int.toHSV(): FloatArray {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    return hsv
}

inline val Int.isColorOpaque: Boolean
    get() = Color.alpha(this) == 255

fun FloatArray.toColor(): Int = Color.HSVToColor(this)

fun Int.isColorVisibleOn(
    @ColorInt color: Int,
    @IntRange(from = 0L, to = 255L) delta: Int = 25,
    @IntRange(from = 0L, to = 255L) minAlpha: Int = 50
): Boolean =
    if (Color.alpha(this) < minAlpha) false
    else !(Math.abs(Color.red(this) - Color.red(color)) < delta &&
        Math.abs(Color.green(this) - Color.green(color)) < delta &&
        Math.abs(Color.blue(this) - Color.blue(color)) < delta)

@ColorInt
fun Context.getDisabledColor(): Int {
    val primaryColor = resolveColor(android.R.attr.textColorPrimary)
    val disabledColor = if (primaryColor.isColorDark) Color.BLACK else Color.WHITE
    return disabledColor.adjustAlpha(0.3f)
}

@ColorInt
fun Int.adjustAlpha(factor: Float): Int {
    val alpha = Math.round(Color.alpha(this) * factor)
    return Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
}

inline val Int.isColorTransparent: Boolean
    get() = Color.alpha(this) != 255

@ColorInt
fun Int.blendWith(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) ratio: Float): Int {
    val inverseRatio = 1f - ratio
    val a = Color.alpha(this) * inverseRatio + Color.alpha(color) * ratio
    val r = Color.red(this) * inverseRatio + Color.red(color) * ratio
    val g = Color.green(this) * inverseRatio + Color.green(color) * ratio
    val b = Color.blue(this) * inverseRatio + Color.blue(color) * ratio
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

@ColorInt
fun Int.withAlpha(@IntRange(from = 0, to = 0xFF) alpha: Int): Int =
    this and 0x00FFFFFF or (alpha shl 24)

@ColorInt
fun Int.withMinAlpha(@IntRange(from = 0, to = 0xFF) alpha: Int): Int =
    withAlpha(Math.max(alpha, this ushr 24))

@ColorInt
private inline fun Int.colorFactor(rgbFactor: (Int) -> Float): Int {
    val (red, green, blue) = intArrayOf(Color.red(this), Color.green(this), Color.blue(this))
        .map { rgbFactor(it).toInt() }
    return Color.argb(Color.alpha(this), red, green, blue)
}

@ColorInt
fun Int.lighten(@FloatRange(from = 0.0, to = 1.0) factor: Float = 0.1f): Int = colorFactor {
    (it * (1f - factor) + 255f * factor)
}

@ColorInt
fun Int.darken(@FloatRange(from = 0.0, to = 1.0) factor: Float = 0.1f): Int = colorFactor {
    it * (1f - factor)
}

@ColorInt
fun Int.colorToBackground(@FloatRange(from = 0.0, to = 1.0) factor: Float = 0.1f): Int =
    if (isColorDark) darken(factor) else lighten(factor)

@ColorInt
fun Int.colorToForeground(@FloatRange(from = 0.0, to = 1.0) factor: Float = 0.1f): Int =
    if (isColorDark) lighten(factor) else darken(factor)

fun String.toColor(): Int {
    val toParse: String = if (startsWith("#") && length == 4)
        "#${this[1]}${this[1]}${this[2]}${this[2]}${this[3]}${this[3]}"
    else
        this
    return Color.parseColor(toParse)
}

// Get ColorStateList
fun Context.colorStateList(@ColorInt color: Int): ColorStateList {
    val disabledColor = color.adjustAlpha(0.3f)
    return ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_enabled, android.R.attr.state_checked)
        ),
        intArrayOf(color.adjustAlpha(0.8f), color, disabledColor, disabledColor)
    )
}

/*
 * Tint Helpers
 * Kotlin tint bindings that start with 'tint' so it doesn't conflict with existing methods
 * Largely based on MDTintHelper
 * https://github.com/afollestad/material-dialogs/blob/master/core/src/main/java/com/afollestad/materialdialogs/internal/MDTintHelper.java
 */
@SuppressLint("PrivateResource")
fun RadioButton.tint(colors: ColorStateList) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        buttonTintList = colors
    } else {
        val radioDrawable = context.drawable(R.drawable.abc_btn_radio_material)
        val d = DrawableCompat.wrap(radioDrawable)
        DrawableCompat.setTintList(d, colors)
        buttonDrawable = d
    }
}

fun RadioButton.tint(@ColorInt color: Int) = tint(context.colorStateList(color))

@SuppressLint("PrivateResource")
fun CheckBox.tint(colors: ColorStateList) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        buttonTintList = colors
    } else {
        val checkDrawable = context.drawable(R.drawable.abc_btn_check_material)
        val drawable = DrawableCompat.wrap(checkDrawable)
        DrawableCompat.setTintList(drawable, colors)
        buttonDrawable = drawable
    }
}

fun CheckBox.tint(@ColorInt color: Int) = tint(context.colorStateList(color))

fun SeekBar.tint(@ColorInt color: Int) {
    val s1 = ColorStateList.valueOf(color)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        thumbTintList = s1
        progressTintList = s1
    } else {
        val progressDrawable = DrawableCompat.wrap(progressDrawable)
        this.progressDrawable = progressDrawable
        DrawableCompat.setTintList(progressDrawable, s1)
        val thumbDrawable = DrawableCompat.wrap(thumb)
        DrawableCompat.setTintList(thumbDrawable, s1)
        thumb = thumbDrawable
    }
}

fun ProgressBar.tint(@ColorInt color: Int, skipIndeterminate: Boolean = false) {
    val sl = ColorStateList.valueOf(color)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        progressTintList = sl
        secondaryProgressTintList = sl
        if (!skipIndeterminate) indeterminateTintList = sl
    } else {
        val mode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN
        indeterminateDrawable?.setColorFilter(color, mode)
        progressDrawable?.setColorFilter(color, mode)
    }
}

fun Context.textColorStateList(@ColorInt color: Int): ColorStateList {
    val states = arrayOf(
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_pressed, -android.R.attr.state_focused),
        intArrayOf()
    )
    val colors = intArrayOf(
        resolveColor(R.attr.colorControlNormal),
        resolveColor(R.attr.colorControlNormal),
        color
    )
    return ColorStateList(states, colors)
}

/**
 * Note that this does not tint the cursor, as there is no public api to do so.
 */
fun EditText.tint(@ColorInt color: Int) {
    val editTextColorStateList = context.textColorStateList(color)
    if (this is AppCompatEditText) {
        ViewCompat.setBackgroundTintList(this, editTextColorStateList)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        backgroundTintList = editTextColorStateList
    }
}

fun Toolbar.tint(@ColorInt color: Int, tintTitle: Boolean = true) {
    if (tintTitle) {
        setTitleTextColor(color)
        setSubtitleTextColor(color)
    }
    (0 until childCount).asSequence().forEach { (getChildAt(it) as? ImageButton)?.setColorFilter(color) }
}
