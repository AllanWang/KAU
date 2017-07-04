package ca.allanwang.kau.permissions

import android.app.Activity
import android.content.Context

/**
 * Created by Allan Wang on 2017-07-02.
 *
 * Bindings for the permission manager
 */

/**
 * Hook that should be added inside all [Activity.onRequestPermissionsResult] so that the Permission manager can handle the responses
 */
fun Activity.kauOnRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray)
        = PermissionManager.onRequestPermissionsResult(this, permissions, grantResults)

/**
 * Request a permission with a callback
 * In reality, an activity is needed to fulfill the request, but a context is enough if those permissions are already granted
 * To be safe, you may want to check that the context can be casted successfully first
 * The [callback] returns [granted], which is true if all permissions are granted
 * [deniedPerm] is the first denied permission, if granted is false
 */
fun Context.requestPermissions(vararg permissions: String, callback: (granted: Boolean, deniedPerm: String?) -> Unit)
        = PermissionManager(this, permissions, callback)