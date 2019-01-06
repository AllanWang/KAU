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
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-05-29.
 */
@KauUtils
fun IIcon.toDrawable(
    c: Context,
    sizeDp: Int = 24,
    @ColorInt color: Int = Color.WHITE,
    builder: IconicsDrawable.() -> Unit = {}
): Drawable {
    val state = ColorStateList.valueOf(color)
    val icon = IconicsDrawable(c).icon(this).color(state)
    if (sizeDp > 0) icon.sizeDp(sizeDp)
    icon.builder()
    return icon
}
