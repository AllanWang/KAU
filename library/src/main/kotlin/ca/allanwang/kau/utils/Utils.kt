package ca.allanwang.kau.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Looper
import ca.allanwang.kau.R
import ca.allanwang.kau.logging.KL


/**
 * Created by Allan Wang on 2017-05-28.
 */

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val buildIsLollipopAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

val buildIsMarshmallowAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

/**
 * Log whether current state is in the main thread
 */
fun checkThread(id: Int) {
    val status = if (Looper.myLooper() == Looper.getMainLooper()) "is" else "is not"
    KL.d("$id $status in the main thread")
}

/**
 * Checks if a given package is installed
 * @param packageName packageId
 * @return true if installed with activity, false otherwise
 */
fun Context.isAppInstalled(packageName: String): Boolean {
    val pm = packageManager
    var installed: Boolean
    try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        installed = true
    } catch (e: PackageManager.NameNotFoundException) {
        installed = false
    }
    return installed
}

/**
 * Converts minute value to string
 * Whole hours and days will be converted as such, otherwise it will default to x minutes
 */
fun Context.minuteToText(minutes: Long): String = with(minutes) {
    if (this < 0L) string(R.string.kau_none)
    else if (this == 60L) string(R.string.kau_one_hour)
    else if (this == 1440L) string(R.string.kau_one_day)
    else if (this % 1440L == 0L) String.format(string(R.string.kau_x_days), this / 1440L)
    else if (this % 60L == 0L) String.format(string(R.string.kau_x_hours), this / 60L)
    else String.format(string(R.string.kau_x_minutes), this)
}