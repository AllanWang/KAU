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

import android.content.Context
import android.graphics.Color
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import ca.allanwang.kau.utils.INVALID_ID
import ca.allanwang.kau.utils.string
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme

class ColorBuilder : ColorContract {
    override var title: String? = null
    override var titleRes: Int = INVALID_ID
    override var allowCustom: Boolean = true
    override var allowCustomAlpha: Boolean = false
    override var isAccent: Boolean = false
    override var defaultColor: Int = Color.BLACK
    override var doneText: Int = R.string.kau_done
    override var backText: Int = R.string.kau_back
    override var cancelText: Int = R.string.kau_cancel
    override var presetText: Int = R.string.kau_md_presets
    override var customText: Int = R.string.kau_custom
        get() = if (allowCustom) field else 0
    override var dynamicButtonColors: Boolean = true
    override var circleSizeRes: Int = R.dimen.kau_color_circle_size
    override var colorCallback: ((selectedColor: Int) -> Unit)? = null
    override var colorsTop: IntArray? = null
    override var colorsSub: Array<IntArray>? = null
    override var theme: Theme? = null
}

interface ColorContract {
    var title: String?
    @setparam:StringRes
    var titleRes: Int
    var allowCustom: Boolean
    var allowCustomAlpha: Boolean
    var isAccent: Boolean
    @setparam:StringRes
    var defaultColor: Int
    @setparam:StringRes
    var doneText: Int
    @setparam:StringRes
    var backText: Int
    @setparam:StringRes
    var cancelText: Int
    @setparam:StringRes
    var presetText: Int
    @setparam:StringRes
    var customText: Int
    var dynamicButtonColors: Boolean
    @setparam:DimenRes
    var circleSizeRes: Int
    var colorCallback: ((selectedColor: Int) -> Unit)?
    var colorsTop: IntArray?
    var colorsSub: Array<IntArray>?
    var theme: Theme?
}

/**
 * This is the extension that allows us to initialize the dialog
 * Note that this returns just the dialog; you still need to call .show() to show it
 */
fun Context.colorPickerDialog(action: ColorContract.() -> Unit): MaterialDialog {
    val b = ColorBuilder()
    b.action()
    return colorPickerDialog(b)
}

fun Context.colorPickerDialog(contract: ColorContract): MaterialDialog {
    val view = ColorPickerView(this)
    val dialog = with(MaterialDialog.Builder(this)) {
        title(string(contract.titleRes, contract.title) ?: string(R.string.kau_md_color_palette))
        customView(view, false)
        autoDismiss(false)
        positiveText(contract.doneText)
        negativeText(contract.cancelText)
        if (contract.allowCustom) neutralText(contract.presetText)
        onPositive { dialog, _ -> contract.colorCallback?.invoke(view.selectedColor); dialog.dismiss() }
        onNegative { _, _ -> view.backOrCancel() }
        if (contract.allowCustom) onNeutral { _, _ -> view.toggleCustom() }
        showListener { view.refreshColors() }
        if (contract.theme != null) theme(contract.theme!!)
        build()
    }
    view.bind(contract, dialog)
    return dialog
}
