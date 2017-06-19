package ca.allanwang.kau.utils

import android.content.res.Resources
import android.os.Build
import android.os.Looper
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