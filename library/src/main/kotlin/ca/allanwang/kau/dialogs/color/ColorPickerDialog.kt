package ca.allanwang.kau.dialogs.color

import android.content.Context
import android.graphics.Color
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme

class Builder {
    var title: String? = null
    var titleRes: Int = -1
    var allowCustom: Boolean = true
    var allowCustomAlpha: Boolean = false
    var isAccent: Boolean = false
    var defaultColor: Int = Color.BLACK
    var doneText: Int = R.string.kau_done
    var backText: Int = R.string.kau_back
    var cancelText: Int = R.string.kau_cancel
    var presetText: Int = R.string.kau_md_presets
    var customText: Int = R.string.kau_md_custom
        get() = if (allowCustom) field else 0
    var dynamicButtonColors: Boolean = true
    var circleSizeRes: Int = R.dimen.kau_color_circle_size
    var colorCallbacks: MutableList<((selectedColor: Int) -> Unit)> = mutableListOf()
    var colorsTop: IntArray? = null
    internal fun colorsTop(): IntArray =
            if (colorsTop != null) colorsTop!!
            else if (isAccent) ColorPalette.ACCENT_COLORS
            else ColorPalette.PRIMARY_COLORS

    var colorsSub: Array<IntArray>? = null
    internal fun colorsSub(): Array<IntArray>? =
            if (colorsTop != null) colorsSub
            else if (isAccent) ColorPalette.ACCENT_COLORS_SUB
            else ColorPalette.PRIMARY_COLORS_SUB

    var theme: Theme? = null

    fun applyNestedBuilder(action: Builder.() -> Unit) = this.action()
}

/**
 * This is the extension that allows us to initialize the dialog
 * Note that this returns just the dialog; you still need to call .show() to show it
 */
fun Context.colorPickerDialog(action: Builder.() -> Unit): MaterialDialog {
    val b = Builder()
    b.action()
    val view = ColorPickerView(this)
    val dialog = with(MaterialDialog.Builder(this)) {
        title(string(b.titleRes, b.title) ?: string(R.string.kau_md_color_palette))
        customView(view, false)
        autoDismiss(false)
        positiveText(b.doneText)
        negativeText(b.cancelText)
        if (b.allowCustom) neutralText(b.presetText)
        onPositive { dialog, _ -> b.colorCallbacks.forEach { it.invoke(view.selectedColor) }; dialog.dismiss() }
        onNegative { dialog, _ -> view.backOrCancel() }
        if (b.allowCustom) onNeutral { dialog, _ -> view.toggleCustom() }
        showListener { view.refreshColors() }
        if (b.theme != null) theme(b.theme!!)
        build()
    }
    view.bind(b, dialog)
    return dialog
}