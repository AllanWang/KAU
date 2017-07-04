package ca.allanwang.kau.permissions

import android.content.pm.PackageManager

/**
 * Created by Allan Wang on 2017-07-03.
 */
class PermissionResult(permissions: Array<out String>, val callback: (granted: Boolean, deniedPerm: String?) -> Unit) {
    val permissions = mutableSetOf(*permissions)

    /**
     * Called from the manager whenever a permission has changed
     * Returns true if result is completed, false otherwise
     */
    fun onResult(permission: String, result: Int): Boolean {
        if (result != PackageManager.PERMISSION_GRANTED) {
            callback(false, permission)
            permissions.clear()
            return true
        }
        permissions.remove(permission)
        if (permissions.isNotEmpty()) return false
        callback(true, null)
        return true
    }
}