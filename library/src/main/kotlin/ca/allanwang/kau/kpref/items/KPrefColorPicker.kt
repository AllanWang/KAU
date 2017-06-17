package ca.allanwang.kau.kpref.items

import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.dialogs.color.CircleView
import ca.allanwang.kau.dialogs.color.ColorBuilder
import ca.allanwang.kau.dialogs.color.ColorContract
import ca.allanwang.kau.dialogs.color.colorPickerDialog
import ca.allanwang.kau.kpref.KPrefAdapterBuilder

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * ColorPicker preference
 * When a color is successfully selected in the dialog, it will be saved as an int
 */
class KPrefColorPicker(adapterBuilder: KPrefAdapterBuilder,
                       val builder: KPrefColorContract
) : KPrefItemBase<Int>(adapterBuilder, builder) {

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        super.onPostBindView(viewHolder, textColor, accentColor)
        builder.apply {
            titleRes = core.titleRes
            colorCallback = {
                pref = it
            }
        }
        if (builder.showPreview) {
            val preview = viewHolder.bindInnerView<CircleView>(R.layout.kau_preference_color_preview)
            preview.setBackgroundColor(pref)
            preview.withBorder = true
            builder.apply {
                colorCallback = {
                    pref = it
                    if (builder.showPreview)
                        preview.setBackgroundColor(it)
                }
            }
        }
    }


    override fun defaultOnClick(itemView: View, innerContent: View?): Boolean {
        builder.apply {
            defaultColor = pref //update color
        }
        itemView.context.colorPickerDialog(builder).show()
        return true
    }

    class KPrefColorBuilder : KPrefColorContract, BaseContract<Int> by BaseBuilder<Int>(), ColorContract by ColorBuilder() {
        override var showPreview: Boolean = true
        override var titleRes: Int = -1
    }

    interface KPrefColorContract : BaseContract<Int>, ColorContract {
        var showPreview: Boolean
    }

    override fun getType(): Int = R.id.kau_item_pref_color_picker

}