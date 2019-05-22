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
package ca.allanwang.kau.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import ca.allanwang.kau.kotlin.kauRemoveIf
import ca.allanwang.kau.kotlin.lazyContext
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.KauException
import ca.allanwang.kau.utils.buildIsMarshmallowAndUp
import ca.allanwang.kau.utils.hasPermission
import ca.allanwang.kau.utils.toast
import java.lang.ref.WeakReference

/**
 * Created by Allan Wang on 2017-07-03.
 *
 * Permission manager that is decoupled from activities
 * Keeps track of pending requests, and warns about invalid requests
 */
internal object PermissionManager {

    private val pendingResults = mutableListOf<WeakReference<PermissionResult>>()

    /**
     * Retrieve permissions requested in our manifest
     */
    private val manifestPermission = lazyContext<Set<String>> {
        try {
            it.packageManager.getPackageInfo(it.packageName, PackageManager.GET_PERMISSIONS)?.requestedPermissions?.toSet()
                ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    operator fun invoke(
        context: Context,
        permissions: Array<out String>,
        callback: (granted: Boolean, deniedPerm: String?) -> Unit
    ) {
        KL.d { "Permission manager for: ${permissions.contentToString()}" }
        if (!buildIsMarshmallowAndUp) return callback(true, null)
        val missingPermissions = permissions.filter { !context.hasPermission(it) }
        if (missingPermissions.isEmpty()) return callback(true, null)
        pendingResults.add(WeakReference(PermissionResult(permissions, callback = callback)))
        if (pendingResults.size == 1) {
            requestPermissions(context, missingPermissions.toTypedArray())
        } else {
            KL.d { "Request is postponed since another one is still in progress; did you remember to override onRequestPermissionsResult?" }
        }
    }

    private fun requestPermissions(context: Context, permissions: Array<out String>) {
        permissions.forEach {
            if (!manifestPermission(context).contains(it)) {
                KL.e { "Requested permission $it is not stated in the manifest" }
                context.toast("$it is not in the manifest")
                //we'll let the request pass through so it can be denied and so the callback can be triggered
            }
        }
        val activity = (context as? Activity)
            ?: throw KauException("Context is not an instance of an activity; cannot request permissions")
        KL.i { "Requesting permissions ${permissions.contentToString()}" }
        ActivityCompat.requestPermissions(activity, permissions, 1)
    }

    /**
     * Handles permission result by allowing accepted permissions for all pending requests
     * Also cleans up destroyed or completed pending requests
     */
    fun onRequestPermissionsResult(context: Context, permissions: Array<out String>, grantResults: IntArray) {
        KL.i { "On permission result: pending ${pendingResults.size}" }
        val count = Math.min(permissions.size, grantResults.size)
        pendingResults.kauRemoveIf {
            val action = it.get()
            action == null || (0 until count).any { i -> action.onResult(permissions[i], grantResults[i]) }
        }
        val action = pendingResults.asSequence().map { it.get() }.firstOrNull { it != null }
        if (action == null) { //actions have been unlinked from their weak references
            pendingResults.clear()
            return
        }
        requestPermissions(context, action.permissions.toTypedArray())
    }
}
