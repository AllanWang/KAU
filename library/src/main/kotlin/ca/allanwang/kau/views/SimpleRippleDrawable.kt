package ca.allanwang.kau.views

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.support.annotation.ColorInt
import ca.allanwang.kau.searchview.SearchItem
import ca.allanwang.kau.utils.adjustAlpha

/**
 * Created by Allan Wang on 2017-06-24.
 *
 * Tries to mimic a standard ripple, given the foreground and background colors
 */
class SimpleRippleDrawable(@ColorInt foregroundColor: Int, @ColorInt backgroundColor: Int
) : RippleDrawable(ColorStateList(arrayOf(intArrayOf()), intArrayOf(foregroundColor)),
        ColorDrawable(backgroundColor), ColorDrawable(foregroundColor.adjustAlpha(0.16f)))