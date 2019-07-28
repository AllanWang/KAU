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
package ca.allanwang.kau.colorpicker

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.annotation.ColorInt
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorCallback
import com.afollestad.materialdialogs.color.colorChooser

sealed class ColorOptions(val colors: IntArray, val subColors: Array<IntArray>?)

object PrimaryColors : ColorOptions(ColorPalette.PRIMARY_COLORS, ColorPalette.PRIMARY_COLORS_SUB)
object AccentColors : ColorOptions(ColorPalette.ACCENT_COLORS, ColorPalette.ACCENT_COLORS_SUB)
class CustomColors(colors: IntArray, subColors: Array<IntArray>? = null) : ColorOptions(colors, subColors)

class ColorBuilder : ColorContract {
    override var colors: ColorOptions = PrimaryColors
    override var allowCustom: Boolean = true
    override var allowCustomAlpha: Boolean = false
    override var defaultColor: Int = Color.BLACK
    override var callback: ColorCallback = null
}

interface ColorContract {
    var colors: ColorOptions
    var allowCustom: Boolean
    var allowCustomAlpha: Boolean
    @setparam:ColorInt
    var defaultColor: Int
    var callback: ColorCallback
}

@SuppressLint("CheckResult")
fun MaterialDialog.kauColorChooser(action: ColorContract.() -> Unit) =
    kauColorChooser(ColorBuilder().apply(action))

/**
 * Thin wrapper that exposes color chooser options as [ColorContract]
 */
@SuppressLint("CheckResult")
fun MaterialDialog.kauColorChooser(c: ColorContract) {
    colorChooser(
        colors = c.colors.colors,
        subColors = c.colors.subColors,
        initialSelection = c.defaultColor,
        allowCustomArgb = c.allowCustom,
        showAlphaSelector = c.allowCustomAlpha,
        selection = c.callback
    )
    positiveButton(R.string.kau_done)
}
