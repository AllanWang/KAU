package ca.allanwang.kau.dialogs.color

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.afollestad.materialdialogs.color.FillGridView
import java.util.*

/**
 * Created by Allan Wang on 2017-06-08.
 */
internal class ColorPickerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {
    var selectedColor: Int = -1
    var isInSub: Boolean = false
    var isInCustom: Boolean = false
    var circleSize: Int = context.dimen(R.dimen.kau_color_circle_size).toInt()
    val backgroundColor = context.resolveColor(R.attr.md_background_color,
            if (context.resolveColor(android.R.attr.textColorPrimary).isColorDark()) Color.WHITE else 0xff424242.toInt())
    val backgroundColorTint = if (backgroundColor.isColorDark()) backgroundColor.lighten(0.2f) else backgroundColor.darken(0.2f)
    lateinit var dialog: MaterialDialog
    lateinit var builder: Builder
    lateinit var colorsTop: IntArray
    var colorsSub: Array<IntArray>? = null
    var topIndex: Int = -1
    var subIndex: Int = -1
    var colorIndex: Int
        get() = if (isInSub) subIndex else topIndex
        set(value) {
            if (isInSub) subIndex = value
            else {
                topIndex = value
                if (colorsSub != null && colorsSub!!.size > value) {
                    dialog.setActionButton(DialogAction.NEGATIVE, builder.backText)
                    isInSub = true
                    invalidateGrid()
                }
            }
        }


    val gridView: FillGridView by bindView(R.id.md_grid)
    val customFrame: LinearLayout by bindView(R.id.md_colorChooserCustomFrame)
    val customColorIndicator: View by bindView(R.id.md_colorIndicator)
    val hexInput: EditText by bindView(R.id.md_hexInput)
    val alphaLabel: TextView by bindView(R.id.md_colorALabel)
    val alphaSeekbar: SeekBar by bindView(R.id.md_colorA)
    val alphaValue: TextView by bindView(R.id.md_colorAValue)
    val redSeekbar: SeekBar by bindView(R.id.md_colorR)
    val redValue: TextView by bindView(R.id.md_colorRValue)
    val greenSeekbar: SeekBar by bindView(R.id.md_colorG)
    val greenValue: TextView by bindView(R.id.md_colorGValue)
    val blueSeekbar: SeekBar by bindView(R.id.md_colorB)
    val blueValue: TextView by bindView(R.id.md_colorBValue)

    var customHexTextWatcher: TextWatcher? = null
    var customRgbListener: SeekBar.OnSeekBarChangeListener? = null

    init {
        View.inflate(context, R.layout.md_dialog_colorchooser, this)
    }

    fun bind(builder: Builder, dialog: MaterialDialog) {
        this.builder = builder
        this.dialog = dialog
        this.colorsTop = builder.colorsTop()
        this.colorsSub = builder.colorsSub()
        this.selectedColor = builder.defaultColor
        if (builder.allowCustom) {
            if (!builder.allowCustomAlpha) {
                alphaLabel.gone()
                alphaSeekbar.gone()
                alphaValue.gone()
                hexInput.hint = String.format("%06X", selectedColor)
                hexInput.filters = arrayOf(InputFilter.LengthFilter(6))
            } else {
                hexInput.hint = String.format("%08X", selectedColor)
                hexInput.filters = arrayOf(InputFilter.LengthFilter(8))
            }
        }
        if (findColor(builder.defaultColor) || !builder.allowCustom) isInCustom = true //when toggled this will be false
        toggleCustom()
    }

    fun backOrCancel() {
        if (isInSub) {
            dialog.setActionButton(DialogAction.NEGATIVE, builder.cancelText)
            //to top
            isInSub = false
            subIndex = -1
            invalidateGrid()
        } else {
            dialog.cancel()
        }
    }

    fun toggleCustom() {
        isInCustom = !isInCustom
        if (isInCustom) {
            isInSub = false
            if (builder.allowCustom) dialog.setActionButton(DialogAction.NEUTRAL, builder.presetText)
            dialog.setActionButton(DialogAction.NEGATIVE, builder.cancelText)
            customHexTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    try {
                        selectedColor = Color.parseColor("#" + s.toString())
                    } catch (e: IllegalArgumentException) {
                        selectedColor = Color.BLACK
                    }

                    customColorIndicator.setBackgroundColor(selectedColor)
                    if (alphaSeekbar.isVisible()) {
                        val alpha = Color.alpha(selectedColor)
                        alphaSeekbar.progress = alpha
                        alphaValue.text = String.format(Locale.CANADA, "%d", alpha)
                    }
                    redSeekbar.progress = Color.red(selectedColor)
                    greenSeekbar.progress = Color.green(selectedColor)
                    blueSeekbar.progress = Color.blue(selectedColor)
                    isInSub = false
                    topIndex = -1
                    subIndex = -1
                    refreshColors()
                }

                override fun afterTextChanged(s: Editable?) {}
            }
            hexInput.setText(selectedColor.toHexString(builder.allowCustomAlpha, false))
            hexInput.addTextChangedListener(customHexTextWatcher)
            customRgbListener = object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val color = if (builder.allowCustomAlpha)
                            Color.argb(alphaSeekbar.progress,
                                    redSeekbar.progress,
                                    greenSeekbar.progress,
                                    blueSeekbar.progress)
                        else Color.rgb(redSeekbar.progress,
                                greenSeekbar.progress,
                                blueSeekbar.progress)

                        hexInput.setText(color.toHexString(builder.allowCustomAlpha, false))
                    }
                    if (builder.allowCustomAlpha) alphaValue.text = alphaSeekbar.progress.toString()
                    redValue.text = redSeekbar.progress.toString()
                    greenValue.text = greenSeekbar.progress.toString()
                    blueValue.text = blueSeekbar.progress.toString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
            redSeekbar.setOnSeekBarChangeListener(customRgbListener)
            greenSeekbar.setOnSeekBarChangeListener(customRgbListener)
            blueSeekbar.setOnSeekBarChangeListener(customRgbListener)
            if (alphaSeekbar.isVisible())
                alphaSeekbar.setOnSeekBarChangeListener(customRgbListener)
            hexInput.setText(selectedColor.toHexString(alphaSeekbar.isVisible(), false))
            gridView.fadeOut(onFinish = { gridView.gone() })
            customFrame.fadeIn()
        } else {
            findColor(selectedColor)
            if (builder.allowCustom) dialog.setActionButton(DialogAction.NEUTRAL, builder.customText)
            dialog.setActionButton(DialogAction.NEGATIVE, if (isInSub) builder.backText else builder.cancelText)
            gridView.fadeIn(onStart = { invalidateGrid() })
            customFrame.fadeOut(onFinish = { customFrame.gone() })
            hexInput.removeTextChangedListener(customHexTextWatcher)
            customHexTextWatcher = null
            alphaSeekbar.setOnSeekBarChangeListener(null)
            redSeekbar.setOnSeekBarChangeListener(null)
            greenSeekbar.setOnSeekBarChangeListener(null)
            blueSeekbar.setOnSeekBarChangeListener(null)
            customRgbListener = null
        }
    }

    fun refreshColors() {
        if (!isInCustom) findColor(selectedColor)
        //Ensure that our tinted color is still visible against the background
        val visibleColor = if (selectedColor.isColorVisibleOn(backgroundColor)) selectedColor else backgroundColorTint
        if (builder.dynamicButtonColors) {
            dialog.getActionButton(DialogAction.POSITIVE).setTextColor(visibleColor)
            dialog.getActionButton(DialogAction.NEGATIVE).setTextColor(visibleColor)
            dialog.getActionButton(DialogAction.NEUTRAL).setTextColor(visibleColor)
        }
        if (!builder.allowCustom || !isInCustom) return
        if (builder.allowCustomAlpha)
            alphaSeekbar.visible().tint(visibleColor)
        redSeekbar.tint(visibleColor)
        greenSeekbar.tint(visibleColor)
        blueSeekbar.tint(visibleColor)
        hexInput.tint(visibleColor)
    }

    fun findColor(@ColorInt color: Int): Boolean {
        topIndex = -1
        subIndex = -1
        colorsTop.forEachIndexed {
            index, topColor ->
            if (findSubColor(color, index)) {
                topIndex = index
                return true
            }
            if (topColor == color) { // If no sub colors exists and top color matches
                topIndex = index
                return true
            }
        }
        return false
    }

    fun findSubColor(@ColorInt color: Int, topIndex: Int): Boolean {
        if (colorsSub == null || colorsSub!!.size <= topIndex) return false
        colorsSub!![topIndex].forEachIndexed {
            index, subColor ->
            if (subColor == color) {
                subIndex = index
                return true
            }
        }
        return false
    }

    fun invalidateGrid() {
        if (gridView.adapter == null) {
            gridView.adapter = ColorGridAdapter()
            gridView.selector = ResourcesCompat.getDrawable(resources, R.drawable.kau_transparent, null)
        } else {
            (gridView.adapter as BaseAdapter).notifyDataSetChanged()
        }
    }

    inner class ColorGridAdapter : BaseAdapter(), OnClickListener, OnLongClickListener {
        override fun onClick(v: View) {
            if (v.tag != null && v.tag is String) {
                val tags = (v.tag as String).split(":")
                if (colorIndex == tags[0].toInt()) {
                    colorIndex = tags[0].toInt() //Go to sub list if exists
                    return
                }
                if (colorIndex != -1) (gridView.getChildAt(colorIndex) as CircleView).animateSelected(false)
                selectedColor = tags[1].toInt()
                refreshColors()
                val currentSub = isInSub
                colorIndex = tags[0].toInt()
                if (currentSub == isInSub) (gridView.getChildAt(colorIndex) as CircleView).animateSelected(true)
                //Otherwise we are invalidating our grid, so there is no point in animating
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (v.tag != null && v.tag is String) {
                val tag = (v.tag as String).split(":")
                val color = tag[1].toInt()
                (v as CircleView).showHint(color)
                return true
            }
            return false
        }

        override fun getItem(position: Int): Any = if (isInSub) colorsSub!![topIndex][position] else colorsTop[position]

        override fun getCount(): Int = if (isInSub) colorsSub!![topIndex].size else colorsTop.size

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: CircleView = if (convertView == null)
                CircleView(context).apply { layoutParams = AbsListView.LayoutParams(circleSize, circleSize) }
            else
                convertView as CircleView
            val color: Int = if (isInSub) colorsSub!![topIndex][position] else colorsTop[position]
            return view.apply {
                setBackgroundColor(color)
                isSelected = colorIndex == position
                tag = "$position:$color"
                setOnClickListener(this@ColorGridAdapter)
                setOnLongClickListener(this@ColorGridAdapter)
            }
        }

    }
}

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