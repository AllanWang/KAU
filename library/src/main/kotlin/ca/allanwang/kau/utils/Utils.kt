package ca.allanwang.kau.utils

import android.content.res.Resources

/**
 * Created by Allan Wang on 2017-05-28.
 */

val dpToPx = fun Int.(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

val pxToDp = fun Int.(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
