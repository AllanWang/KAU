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
package ca.allanwang.kau.ui

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import ca.allanwang.kau.utils.adjustAlpha

/**
 * Created by Allan Wang on 2017-06-24.
 *
 * Tries to mimic a standard ripple, given the foreground and background colors
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun createSimpleRippleDrawable(
    @ColorInt foregroundColor: Int,
    @ColorInt backgroundColor: Int
): RippleDrawable {
  val states = ColorStateList(arrayOf(intArrayOf()), intArrayOf(foregroundColor))
  val content = ColorDrawable(backgroundColor)
  val mask = ColorDrawable(foregroundColor.adjustAlpha(0.16f))
  return RippleDrawable(states, content, mask)
}
