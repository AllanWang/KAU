/*
 * Copyright 2019 Allan Wang
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

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Created by Allan Wang on 2017-07-02.
 *
 * Bindings for the permission manager This is the only class you need to worry about when using
 * KAU's manager
 *
 * MAKE SURE [kauOnRequestPermissionsResult] is added to your activities, and don't forget to
 * request the permissions in your manifest. A collection of constants redirecting to the
 * [Manifest.permission] counterparts are added for your convenience
 */

/**
 * Hook that should be added inside all [Activity.onRequestPermissionsResult] so that the Permission
 * manager can handle the responses
 */
fun Activity.kauOnRequestPermissionsResult(permissions: Array<out String>, grantResults: IntArray) =
  PermissionManager.onRequestPermissionsResult(this, permissions, grantResults)

/**
 * Request a permission with a callback In reality, an activity is needed to fulfill the request,
 * but a context is enough if those permissions are already granted To be safe, you may want to
 * check that the context can be casted successfully first The [callback] returns [granted], which
 * is true if all permissions are granted [deniedPerm] is the first denied permission, if granted is
 * false
 */
fun Context.kauRequestPermissions(
  vararg permissions: String,
  callback: (granted: Boolean, deniedPerm: String?) -> Unit
) = PermissionManager(this, permissions, callback)

/**
 * See http://developer.android.com/guide/topics/security/permissions.html#normal-dangerous for a
 * list of 'dangerous' permissions that require a permission request on API 23.
 */
const val PERMISSION_READ_CALENDAR = Manifest.permission.READ_CALENDAR

const val PERMISSION_WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR

const val PERMISSION_CAMERA = Manifest.permission.CAMERA

const val PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS
const val PERMISSION_WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS
const val PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS

const val PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
const val PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

const val PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO

const val PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
const val PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE
const val PERMISSION_READ_CALL_LOG = Manifest.permission.READ_CALL_LOG
const val PERMISSION_WRITE_CALL_LOG = Manifest.permission.WRITE_CALL_LOG
const val PERMISSION_ADD_VOICEMAIL = Manifest.permission.ADD_VOICEMAIL
const val PERMISSION_USE_SIP = Manifest.permission.USE_SIP
@Deprecated(level = DeprecationLevel.WARNING, message = "Permission is deprecated")
@Suppress("DEPRECATION")
const val PERMISSION_PROCESS_OUTGOING_CALLS = Manifest.permission.PROCESS_OUTGOING_CALLS

@RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
const val PERMISSION_BODY_SENSORS = Manifest.permission.BODY_SENSORS

const val PERMISSION_SEND_SMS = Manifest.permission.SEND_SMS
const val PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS
const val PERMISSION_READ_SMS = Manifest.permission.READ_SMS
const val PERMISSION_RECEIVE_WAP_PUSH = Manifest.permission.RECEIVE_WAP_PUSH
const val PERMISSION_RECEIVE_MMS = Manifest.permission.RECEIVE_MMS

const val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
const val PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

const val PERMISSION_SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW
