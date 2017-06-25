package ca.allanwang.kau.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Looper
import android.support.annotation.IntRange
import ca.allanwang.kau.R
import ca.allanwang.kau.logging.KL
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * Created by Allan Wang on 2017-05-28.
 */

/**
 * Markers to isolate respective extension @KauUtils functions to their extended class
 * Avoids having a whole bunch of methods for nested calls
 */
@DslMarker
annotation class KauUtils

@KauUtils val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

@KauUtils val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

/**
 * Log whether current state is in the main thread
 */
@KauUtils fun checkThread(id: Int) {
    val status = if (Looper.myLooper() == Looper.getMainLooper()) "is" else "is not"
    KL.d("$id $status in the main thread")
}

/**
 * Converts minute value to string
 * Whole hours and days will be converted as such, otherwise it will default to x minutes
 */
@KauUtils fun Context.minuteToText(minutes: Long): String = with(minutes) {
    if (this < 0L) string(R.string.kau_none)
    else if (this == 60L) string(R.string.kau_one_hour)
    else if (this == 1440L) string(R.string.kau_one_day)
    else if (this % 1440L == 0L) String.format(string(R.string.kau_x_days), this / 1440L)
    else if (this % 60L == 0L) String.format(string(R.string.kau_x_hours), this / 60L)
    else String.format(string(R.string.kau_x_minutes), this)
}

@KauUtils fun Number.round(@IntRange(from = 1L) decimalCount: Int): String {
    val expression = StringBuilder().append("#.")
    (1..decimalCount).forEach { expression.append("#") }
    val formatter = DecimalFormat(expression.toString())
    formatter.roundingMode = RoundingMode.HALF_UP
    return formatter.format(this)
}