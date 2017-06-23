package ca.allanwang.kau.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.Toolbar
import android.widget.*
import com.afollestad.materialdialogs.R

/**
 * Created by Allan Wang on 2017-06-08.
 */
fun Int.isColorDark(): Boolean
        = (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255.0 < 0.5

fun Int.toHexString(withAlpha: Boolean = false, withHexPrefix: Boolean = true): String {
    val hex = if (withAlpha) String.format("#%08X", this)
    else String.format("#%06X", 0xFFFFFF and this)
    return if (withHexPrefix) hex else hex.substring(1)
}

fun Int.toRgbaString() :String = "rgba(${Color.red(this)}, ${Color.green(this)}, ${Color.blue(this)}, ${(Color.alpha(this)/255f).round(3)})"

fun Int.toHSV(): FloatArray {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    return hsv
}

fun FloatArray.toColor(): Int = Color.HSVToColor(this)

fun Int.isColorVisibleOn(@ColorInt color: Int, @IntRange(from = 0L, to = 255L) delta: Int = 25,
                         @IntRange(from = 0L, to = 255L) minAlpha: Int = 50): Boolean =
        if (Color.alpha(this) < minAlpha) false
        else !(Math.abs(Color.red(this) - Color.red(color)) < delta
                && Math.abs(Color.green(this) - Color.green(color)) < delta
                && Math.abs(Color.blue(this) - Color.blue(color)) < delta)


@ColorInt
fun Context.getDisabledColor(): Int {
    val primaryColor = resolveColor(android.R.attr.textColorPrimary)
    val disabledColor = if (primaryColor.isColorDark()) Color.BLACK else Color.WHITE
    return disabledColor.adjustAlpha(0.3f)
}

@ColorInt
fun Int.adjustAlpha(factor: Float): Int {
    val alpha = Math.round(Color.alpha(this) * factor)
    return Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
}

val Int.isColorTransparent: Boolean
    get() = Color.alpha(this) != 255

@ColorInt
fun Int.withAlpha(@IntRange(from = 0L, to = 255L) alpha: Int): Int
        = Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))

@ColorInt
fun Int.withMinAlpha(@IntRange(from = 0L, to = 255L) alpha: Int): Int
        = Color.argb(Math.max(alpha, Color.alpha(this)), Color.red(this), Color.green(this), Color.blue(this))

@ColorInt
fun Int.lighten(@FloatRange(from = 0.0, to = 1.0) factor: Float = 0.1f): Int {
    val (red, green, blue) = intArrayOf(Color.red(this), Color.green(this), Color.blue(this))
            .map { (it * (1f - factor) + 255f * factor).toInt() }
    return Color.argb(Color.alpha(this), red, green, blue)
}

@ColorInt
fun Int.darken(@FloatRange(from = 0.0, to = 1.0) factor: Float = 0.1f): Int {
    val (red, green, blue) = intArrayOf(Color.red(this), Color.green(this), Color.blue(this))
            .map { (it * (1f - factor)).toInt() }
    return Color.argb(Color.alpha(this), red, green, blue)
}

@ColorInt
fun Int.colorToBackground(@FloatRange(from = 0.0, to = 1.0) factor: Float = 0.1f): Int
        = if (isColorDark()) darken(factor) else lighten(factor)

@ColorInt
fun Int.colorToForeground(@FloatRange(from = 0.0, to = 1.0) factor: Float = 0.1f): Int
        = if (isColorDark()) lighten(factor) else darken(factor)

@Throws(IllegalArgumentException::class)
fun String.toColor(): Int {
    val toParse: String
    if (startsWith("#") && length == 4)
        toParse = "#${this[1]}${this[1]}${this[2]}${this[2]}${this[3]}${this[3]}"
    else
        toParse = this
    return Color.parseColor(toParse)
}

//Get ColorStateList
fun Context.colorStateList(@ColorInt color: Int): ColorStateList {
    val disabledColor = color.adjustAlpha(0.3f)
    return ColorStateList(arrayOf(intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_enabled, android.R.attr.state_checked)),
            intArrayOf(color.adjustAlpha(0.8f), color, disabledColor, disabledColor))
}

/*
 * Tint Helpers
 * Kotlin tint bindings that start with 'tint' so it doesn't conflict with existing methods
 * Largely based on MDTintHelper
 * https://github.com/afollestad/material-dialogs/blob/master/core/src/main/java/com/afollestad/materialdialogs/internal/MDTintHelper.java
 */
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
    } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
        val progressDrawable = DrawableCompat.wrap(progressDrawable)
        this.progressDrawable = progressDrawable
        DrawableCompat.setTintList(progressDrawable, s1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val thumbDrawable = DrawableCompat.wrap(thumb)
            DrawableCompat.setTintList(thumbDrawable, s1)
            thumb = thumbDrawable
        }
    } else {
        val mode: PorterDuff.Mode = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
            PorterDuff.Mode.MULTIPLY else PorterDuff.Mode.SRC_IN
        indeterminateDrawable?.setColorFilter(color, mode)
        progressDrawable?.setColorFilter(color, mode)
    }
}

fun ProgressBar.tint(@ColorInt color: Int, skipIndeterminate: Boolean = false) {
    val sl = ColorStateList.valueOf(color)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        progressTintList = sl
        secondaryProgressTintList = sl
        if (!skipIndeterminate) indeterminateTintList = sl
    } else {
        val mode: PorterDuff.Mode = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
            PorterDuff.Mode.MULTIPLY else PorterDuff.Mode.SRC_IN
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

fun EditText.tint(@ColorInt color: Int) {
    val editTextColorStateList = context.textColorStateList(color)
    if (this is AppCompatEditText) {
        supportBackgroundTintList = editTextColorStateList
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        backgroundTintList = editTextColorStateList
    }
    tintCursor(color)
}

fun EditText.tintCursor(@ColorInt color: Int) {
    try {
        val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
        fCursorDrawableRes.isAccessible = true
        val mCursorDrawableRes = fCursorDrawableRes.getInt(this)
        val fEditor = TextView::class.java.getDeclaredField("mEditor")
        fEditor.isAccessible = true
        val editor = fEditor.get(this)
        val clazz = editor.javaClass
        val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
        fCursorDrawable.isAccessible = true
        val drawables: Array<Drawable> = Array(2, {
            val drawable = ContextCompat.getDrawable(context, mCursorDrawableRes)
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            drawable
        })
        fCursorDrawable.set(editor, drawables)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Toolbar.tint(@ColorInt color: Int, tintTitle: Boolean = true) {
    if (tintTitle) {
        setTitleTextColor(color)
        setSubtitleTextColor(color)
    }
    (0 until childCount).asSequence().forEach { (getChildAt(it) as? ImageButton)?.setColorFilter(color) }
}