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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Pair
import android.view.View
import androidx.annotation.AnimRes
import ca.allanwang.kau.R
import java.io.Serializable

/** Created by Allan Wang on 10/12/17. */
/** Similar to [Bundle.putAll], but checks for a null insert and returns the parent bundle */
infix fun Bundle.with(bundle: Bundle?): Bundle {
  if (bundle != null) putAll(bundle)
  return this
}

/**
 * Saves all bundle args based on their respective types.
 *
 * Taken courtesy of <a href="https://github.com/Kotlin/anko">Anko</a>
 *
 * Previously, Anko was a dependency in KAU, but has been removed on 12/24/2018 as most of the
 * methods weren't used
 */
fun bundleOf(vararg params: kotlin.Pair<String, Any?>): Bundle {
  val b = Bundle()
  for (p in params) {
    val (k, v) = p
    when (v) {
      null -> b.putSerializable(k, null)
      is Boolean -> b.putBoolean(k, v)
      is Byte -> b.putByte(k, v)
      is Char -> b.putChar(k, v)
      is Short -> b.putShort(k, v)
      is Int -> b.putInt(k, v)
      is Long -> b.putLong(k, v)
      is Float -> b.putFloat(k, v)
      is Double -> b.putDouble(k, v)
      is String -> b.putString(k, v)
      is CharSequence -> b.putCharSequence(k, v)
      is Parcelable -> b.putParcelable(k, v)
      is Serializable -> b.putSerializable(k, v)
      is BooleanArray -> b.putBooleanArray(k, v)
      is ByteArray -> b.putByteArray(k, v)
      is CharArray -> b.putCharArray(k, v)
      is DoubleArray -> b.putDoubleArray(k, v)
      is FloatArray -> b.putFloatArray(k, v)
      is IntArray -> b.putIntArray(k, v)
      is LongArray -> b.putLongArray(k, v)
      is Array<*> -> {
        @Suppress("UNCHECKED_CAST")
        when {
          v.isArrayOf<Parcelable>() -> b.putParcelableArray(k, v as Array<out Parcelable>)
          v.isArrayOf<CharSequence>() -> b.putCharSequenceArray(k, v as Array<out CharSequence>)
          v.isArrayOf<String>() -> b.putStringArray(k, v as Array<out String>)
          else -> throw KauException("Unsupported bundle component (${v.javaClass})")
        }
      }
      is ShortArray -> b.putShortArray(k, v)
      is Bundle -> b.putBundle(k, v)
      else -> throw KauException("Unsupported bundle component (${v.javaClass})")
    }
  }
  return b
}

/** Given the parent view and map of view ids to tags, create a scene transition animation */
fun Bundle.withSceneTransitionAnimation(parent: View, data: Map<Int, String>) =
  withSceneTransitionAnimation(
    parent.context,
    data.mapKeys { (id, _) -> parent.findViewById<View>(id) }
  )

/** Given a mapping of views to tags, create a scene transition animation */
@SuppressLint("NewApi")
fun Bundle.withSceneTransitionAnimation(
  context: Context,
  data: Map<out View, String> = emptyMap()
) {
  if (context !is Activity || !buildIsLollipopAndUp) return
  val options =
    ActivityOptions.makeSceneTransitionAnimation(
      context,
      *data.map { (view, tag) -> Pair(view, tag) }.toTypedArray()
    )
  putAll(options.toBundle())
}

fun Bundle.withCustomAnimation(
  context: Context,
  @AnimRes enterResId: Int,
  @AnimRes exitResId: Int
) {
  this with ActivityOptions.makeCustomAnimation(context, enterResId, exitResId).toBundle()
}

fun Bundle.withSlideIn(context: Context) =
  withCustomAnimation(context, R.anim.kau_slide_in_right, R.anim.kau_fade_out)

fun Bundle.withSlideOut(context: Context) =
  withCustomAnimation(context, R.anim.kau_fade_in, R.anim.kau_slide_out_right_top)

fun Bundle.withFade(context: Context) =
  withCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out)
