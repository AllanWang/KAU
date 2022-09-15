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
@file:Suppress("NOTHING_TO_INLINE")

package ca.allanwang.kau.utils

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import ca.allanwang.kau.R
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.iconics.typeface.IIcon
import kotlin.system.exitProcess

/** Created by Allan Wang on 2017-06-21. */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class KauActivity

/**
 * Helper class to launch an activity for result Counterpart of [Activity.startActivityForResult]
 * For starting activities without result, see [startActivity]
 */
@Suppress("DEPRECATION")
inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    bundleBuilder: Bundle.() -> Unit = {},
    intentBuilder: Intent.() -> Unit = {}
) = startActivityForResult(T::class.java, requestCode, bundleBuilder, intentBuilder)

@Deprecated(
    "Use reified generic instead of passing class",
    ReplaceWith("startActivityForResult<T>(requestCode, bundleBuilder, intentBuilder)"),
    DeprecationLevel.WARNING)
inline fun <T : Activity> Activity.startActivityForResult(
    clazz: Class<T>,
    requestCode: Int,
    bundleBuilder: Bundle.() -> Unit = {},
    intentBuilder: Intent.() -> Unit = {}
) {
  val intent = Intent(this, clazz)
  intent.intentBuilder()
  val bundle = Bundle()
  bundle.bundleBuilder()
  startActivityForResult(intent, requestCode, if (bundle.isEmpty) null else bundle)
}

/**
 * Restarts an activity from itself with a fade animation Keeps its existing extra bundles and has a
 * intentBuilder to accept other parameters
 */
inline fun Activity.restart(intentBuilder: Intent.() -> Unit = {}) {
  val i = Intent(this, this::class.java)
  val oldExtras = intent.extras
  if (oldExtras != null) i.putExtras(oldExtras)
  i.intentBuilder()
  startActivity(i)
  overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_fade_out) // No transitions
  finish()
  overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_fade_out)
}

/** Force restart an entire application */
@RequiresApi(Build.VERSION_CODES.M)
inline fun Activity.restartApplication() {
  val intent = packageManager.getLaunchIntentForPackage(packageName)!!
  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
  val pending = PendingIntent.getActivity(this, 666, intent, PendingIntent.FLAG_CANCEL_CURRENT)
  val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
  if (buildIsMarshmallowAndUp)
      alarm.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + 100, pending)
  else alarm.setExact(AlarmManager.RTC, System.currentTimeMillis() + 100, pending)
  finish()
  exitProcess(0)
}

fun Activity.finishSlideOut() {
  finish()
  overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_slide_out_right_top)
}

inline var Activity.navigationBarColor: Int
  get() =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.navigationBarColor
      else Color.BLACK
  set(value) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return
    }
    window.navigationBarColor = value
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return
    }
    var prevSystemUiVisibility = window.decorView.systemUiVisibility
    prevSystemUiVisibility =
        if (value.isColorDark) {
          prevSystemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        } else {
          prevSystemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    window.decorView.systemUiVisibility = prevSystemUiVisibility
  }

inline var Activity.statusBarColor: Int
  get() =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor
      else Color.BLACK
  set(value) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return
    }
    window.statusBarColor = value
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return
    }
    var prevSystemUiVisibility = window.decorView.systemUiVisibility
    prevSystemUiVisibility =
        if (value.isColorDark) {
          prevSystemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
          prevSystemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    window.decorView.systemUiVisibility = prevSystemUiVisibility
  }

/**
 * Themes the base menu icons and adds iicons programmatically based on ids
 *
 * Call in [Activity.onCreateOptionsMenu]
 */
fun Context.setMenuIcons(
    menu: Menu,
    @ColorInt color: Int = Color.WHITE,
    vararg iicons: Pair<Int, IIcon>
) {
  iicons.forEach { (id, iicon) ->
    menu.findItem(id).icon = iicon.toDrawable(this, sizeDp = 18, color = color)
  }
}

inline fun Activity.hideKeyboard() {
  currentFocus?.hideKeyboard()
}

inline fun Activity.showKeyboard() {
  currentFocus?.showKeyboard()
}

/**
 * Gets the view set by [Activity.setContentView] if it exists.
 *
 * Taken courtesy of <a href="https://github.com/Kotlin/anko">Anko</a>
 *
 * Previously, Anko was a dependency in KAU, but has been removed on 12/24/2018 as most of the
 * methods weren't used
 */
inline val Activity.contentView: View?
  get() = (findViewById(android.R.id.content) as? ViewGroup)?.getChildAt(0)

inline fun Activity.snackbar(
    text: String,
    duration: Int = Snackbar.LENGTH_LONG,
    noinline builder: Snackbar.() -> Unit = {}
) = contentView!!.snackbar(text, duration, builder)

inline fun Activity.snackbar(
    @StringRes textId: Int,
    duration: Int = Snackbar.LENGTH_LONG,
    noinline builder: Snackbar.() -> Unit = {}
) = contentView!!.snackbar(textId, duration, builder)
