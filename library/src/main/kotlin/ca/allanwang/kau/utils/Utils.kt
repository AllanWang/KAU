package ca.allanwang.kau.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Looper
import ca.allanwang.kau.logging.KL
import android.content.pm.PackageManager



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