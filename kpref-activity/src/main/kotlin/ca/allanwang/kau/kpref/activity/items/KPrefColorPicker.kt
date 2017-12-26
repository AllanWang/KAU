package ca.allanwang.kau.kpref.activity.items

import ca.allanwang.kau.colorpicker.CircleView
import ca.allanwang.kau.colorpicker.ColorBuilder
import ca.allanwang.kau.colorpicker.ColorContract
import ca.allanwang.kau.colorpicker.colorPickerDialog
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.R
import ca.allanwang.kau.utils.string

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * ColorPicker preference
 * When a color is successfully selected in the dialog, it will be saved as an int
 */
open class KPrefColorPicker(open val builder: KPrefColorContract) : KPrefItemBase<Int>(builder) {

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        super.onPostBindView(viewHolder, textColor, accentColor)
        builder.apply {
            titleRes = core.titleFun()
            colorCallback = { pref = it }
        }
        if (builder.showPreview) {
            val preview = viewHolder.bindInnerView<CircleView>(R.layout.kau_pref_color)
            preview.setBackgroundColor(pref)
            preview.withBorder = true
            builder.apply {
                colorCallback = {
                    pref = it
                    if (builder.showPreview)
                        preview.setBackgroundColor(it)
                    viewHolder.updateTitle()
                    viewHolder.updateDesc()
                }
            }
        }
    }

    override fun KClick<Int>.defaultOnClick() {
        builder.defaultColor = pref
        context.colorPickerDialog(builder).show()
    }

    /**
     * Extension of the base contract and [ColorContract] along with a showPreview option
     */
    interface KPrefColorContract : BaseContract<Int>, ColorContract {
        var showPreview: Boolean
    }

    /**
     * Default implementation of [KPrefColorContract]
     */
    class KPrefColorBuilder(globalOptions: GlobalOptions,
                            titleId: Int,
                            getter: () -> Int,
                            setter: (value: Int) -> Unit
    ) : KPrefColorContract, BaseContract<Int> by BaseBuilder(globalOptions, titleId, getter, setter),
            ColorContract by ColorBuilder() {
        override var showPreview: Boolean = true
    }

    override fun getType(): Int = R.id.kau_item_pref_color_picker

}