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

import android.content.pm.PackageManager

/**
 * Created by Allan Wang on 2017-07-03.
 *
 * Pending permission collector
 */
class PermissionResult(
    permissions: Array<out String>,
    val callback: (granted: Boolean, deniedPerm: String?) -> Unit
) {
  val permissions = mutableSetOf(*permissions)

  /**
   * Called from the manager whenever a permission has changed Returns true if result is completed,
   * false otherwise
   */
  fun onResult(permission: String, result: Int): Boolean {
    if (result != PackageManager.PERMISSION_GRANTED) {
      callback(false, permission)
      permissions.clear()
      return true
    }
    permissions.remove(permission)
    if (permissions.isNotEmpty()) {
      return false
    }
    callback(true, null)
    return true
  }
}
