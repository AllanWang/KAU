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

import android.content.Context
import android.graphics.Typeface

/** Created by Allan Wang on 2017-06-28. */
object FontUtils {

  private val sTypefaceCache: MutableMap<String, Typeface> = mutableMapOf()

  fun get(context: Context, font: String): Typeface {
    synchronized(sTypefaceCache) {
      if (!sTypefaceCache.containsKey(font)) {
        val tf = Typeface.createFromAsset(context.applicationContext.assets, "fonts/$font.ttf")
        sTypefaceCache.put(font, tf)
      }
      return sTypefaceCache.get(font)
          ?: throw IllegalArgumentException(
              "Font error; typeface does not exist at assets/fonts$font.ttf")
    }
  }

  fun getName(typeface: Typeface): String? =
      sTypefaceCache.entries.firstOrNull { it.value == typeface }?.key
}

fun Context.getFont(font: String) = FontUtils.get(this, font)

fun Context.getFontName(typeface: Typeface) = FontUtils.getName(typeface)
