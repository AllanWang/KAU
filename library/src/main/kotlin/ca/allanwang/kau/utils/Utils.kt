package ca.allanwang.kau.utils

import android.content.res.Resources

/**
 * Created by Allan Wang on 2017-05-28.
 */

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.pxToDp(px: Int) = (px / android.content.res.Resources.getSystem().displayMetrics.density).toInt()