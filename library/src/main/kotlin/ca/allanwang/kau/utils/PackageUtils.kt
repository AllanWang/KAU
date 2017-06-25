package ca.allanwang.kau.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * Created by Allan Wang on 2017-06-23.
 */

/**
 * Checks if a given package is installed
 * @param packageName packageId
 * @return true if installed with activity, false otherwise
 */
@KauUtils fun Context.isAppInstalled(packageName: String): Boolean {
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

val buildIsLollipopAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

val buildIsMarshmallowAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

const val INSTALLER_GOOGLE_PLAY_VENDING = "com.android.vending"
const val INSTALLER_GOOGLE_PLAY_FEEDBACK = "com.google.android.feedback"

val Context.installerPackageName: String?
    get() = packageManager.getInstallerPackageName(packageName)

val Context.isFromGooglePlay: Boolean
    get() {
        val installer = installerPackageName
        return arrayOf(INSTALLER_GOOGLE_PLAY_FEEDBACK, INSTALLER_GOOGLE_PLAY_VENDING).any { it == installer }
    }