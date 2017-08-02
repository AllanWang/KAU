package ca.allanwang.kau.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import ca.allanwang.kau.kotlin.lazyContext
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.KauException
import ca.allanwang.kau.utils.buildIsMarshmallowAndUp
import ca.allanwang.kau.utils.hasPermission
import ca.allanwang.kau.utils.toast
import java.lang.ref.WeakReference


/**
 * Created by Allan Wang on 2017-07-03.
 */
internal object PermissionManager {

    var requestInProgress = false
    val pendingResults: MutableList<WeakReference<PermissionResult>> by lazy { mutableListOf<WeakReference<PermissionResult>>() }

    /**
     * Retrieve permissions requested in our manifest
     */
    val manifestPermission = lazyContext<Array<String>> {
        try {
            it.packageManager.getPackageInfo(it.packageName, PackageManager.GET_PERMISSIONS)?.requestedPermissions ?: emptyArray()
        } catch (e: Exception) {
            emptyArray()
        }
    }

    operator fun invoke(context: Context, permissions: Array<out String>, callback: (granted: Boolean, deniedPerm: String?) -> Unit) {
        KL.d("Permission manager for: ${permissions.contentToString()}")
        if (!buildIsMarshmallowAndUp) return callback(true, null)
        val missingPermissions = permissions.filter { !context.hasPermission(it) }
        if (missingPermissions.isEmpty()) return callback(true, null)
        pendingResults.add(WeakReference(PermissionResult(permissions, callback = callback)))
        if (!requestInProgress) {
            requestInProgress = true
            requestPermissions(context, missingPermissions.toTypedArray())
        } else KL.d("Request is postponed since another one is still in progress; did you remember to override onRequestPermissionsResult?")
    }

    @Synchronized internal fun requestPermissions(context: Context, permissions: Array<out String>) {
        permissions.forEach {
            if (!manifestPermission(context).contains(it)) {
                KL.e("Requested permission $it is not stated in the manifest")
                context.toast("$it is not in the manifest")
                //we'll let the request pass through so it can be denied and so the callback can be triggered
            }
        }
        val activity = (context as? Activity) ?: throw KauException("Context is not an instance of an activity; cannot request permissions")
        KL.d("Requesting permissions ${permissions.contentToString()}")
        ActivityCompat.requestPermissions(activity, permissions, 1)
    }

    fun onRequestPermissionsResult(context: Context, permissions: Array<out String>, grantResults: IntArray) {
        KL.d("On permission result: pending ${pendingResults.size}")
        val count = Math.min(permissions.size, grantResults.size)
        val iter = pendingResults.iterator()
        while (iter.hasNext()) {
            val action = iter.next().get()
            if ((0 until count).any { action?.onResult(permissions[it], grantResults[it]) ?: true })
                iter.remove()
        }
        if (pendingResults.isEmpty())
            requestInProgress = false
        else {
            val action = pendingResults.map { it.get() }.firstOrNull { it != null }
            if (action == null) { //actions have been unlinked from their weak references
                pendingResults.clear()
                requestInProgress = false
                return
            }
            requestPermissions(context, action.permissions.toTypedArray())
        }
        KL.d("Post on permission result: pending ${pendingResults.size}")
    }

}