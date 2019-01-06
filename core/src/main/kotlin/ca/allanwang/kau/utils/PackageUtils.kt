/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Created by Allan Wang on 2017-06-23.
 */

/**
 * Checks if a given package is installed
 * @param packageName packageId
 * @return true if installed with activity, false otherwise
 */
@KauUtils
fun Context.isAppInstalled(packageName: String): Boolean {
    try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
        return false
    }
}

@KauUtils
fun Context.isAppEnabled(packageName: String): Boolean {
    try {
        return packageManager.getApplicationInfo(packageName, 0).enabled
    } catch (e: Exception) {
        return false
    }
}

@KauUtils
fun Context.showAppInfo(packageName: String) {
    try {
        //Open the specific App Info page:
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        //Open the generic Apps page:
        val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
        startActivity(intent)
    }
}

inline val buildIsMarshmallowAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

inline val buildIsLollipopAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

inline val buildIsNougatAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

const val INSTALLER_GOOGLE_PLAY_VENDING = "com.android.vending"
const val INSTALLER_GOOGLE_PLAY_FEEDBACK = "com.google.android.feedback"

inline val Context.installerPackageName: String?
    get() = packageManager.getInstallerPackageName(packageName)

inline val Context.isFromGooglePlay: Boolean
    get() {
        val installer = installerPackageName
        return arrayOf(INSTALLER_GOOGLE_PLAY_FEEDBACK, INSTALLER_GOOGLE_PLAY_VENDING).any { it == installer }
    }
