package ca.allanwang.kau.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.mikepenz.iconics.typeface.IIcon


/**
 * Created by Allan Wang on 2017-05-31.
 */
fun <T : View> T.visible(): T {
    visibility = View.VISIBLE
    return this
}

fun <T : View> T.invisible(): T {
    visibility = View.INVISIBLE
    return this
}

fun <T : View> T.gone(): T {
    visibility = View.GONE
    return this
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE
fun View.isInvisible(): Boolean = visibility == View.INVISIBLE
fun View.isGone(): Boolean = visibility == View.GONE

fun View.matchParent() {
    with(layoutParams) {
        height = ViewGroup.LayoutParams.MATCH_PARENT
        width = ViewGroup.LayoutParams.MATCH_PARENT
    }
}

fun ProgressBar.tintRes(@ColorRes id: Int) = tint(ContextCompat.getColor(context, id))

fun ProgressBar.tint(@ColorInt color: Int) {
    val sl = ColorStateList.valueOf(color)
    progressTintList = sl
    secondaryProgressTintList = sl
    indeterminateTintList = sl
}

fun View.snackbar(text: String, duration: Int = Snackbar.LENGTH_LONG, builder: (Snackbar) -> Unit = {}) {
    val snackbar = Snackbar.make(this, text, duration)
    builder.invoke(snackbar)
    snackbar.show()
}

fun View.snackbar(@StringRes textId: Int, duration: Int = Snackbar.LENGTH_LONG, builder: (Snackbar) -> Unit = {})
        = snackbar(context.string(textId), duration, builder)

fun TextView.setTextIfValid(@StringRes id: Int) {
    if (id > 0) text = context.string(id)
}

fun ImageView.setIcon(icon: IIcon?, sizeDp: Int = 24, @ColorInt color: Int = Color.WHITE) {
    if (icon == null) return
    setImageDrawable(icon.toDrawable(context, sizeDp, color))
}

