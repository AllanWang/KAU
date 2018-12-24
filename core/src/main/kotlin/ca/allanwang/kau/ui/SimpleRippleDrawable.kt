package ca.allanwang.kau.ui

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import ca.allanwang.kau.utils.adjustAlpha

/**
 * Created by Allan Wang on 2017-06-24.
 *
 * Tries to mimic a standard ripple, given the foreground and background colors
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun createSimpleRippleDrawable(@ColorInt foregroundColor: Int, @ColorInt backgroundColor: Int): RippleDrawable {
    val states = ColorStateList(arrayOf(intArrayOf()), intArrayOf(foregroundColor))
    val content = ColorDrawable(backgroundColor)
    val mask = ColorDrawable(foregroundColor.adjustAlpha(0.16f))
    return RippleDrawable(states, content, mask)
}