package ca.allanwang.kau.kpref.items

import android.support.annotation.StringRes
import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.dialogs.color.CircleView
import ca.allanwang.kau.dialogs.color.colorPickerDialog
import ca.allanwang.kau.kpref.KPrefAdapterBuilder

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * ColorPicker preference
 * When a color is successfully selected in the dialog, it will be saved as an int
 */
class KPrefColorPicker(builder: KPrefAdapterBuilder,
                       @StringRes title: Int,
                       coreBuilder: KPrefItemCore.Builder.() -> Unit = {},
                       itemBuilder: KPrefItemBase.Builder<Int>.() -> Unit = {},
                       val colorBuilder: ca.allanwang.kau.dialogs.color.Builder.() -> Unit = {},
                       val showPreview: Boolean = false
) : KPrefItemBase<Int>(builder, title, coreBuilder, itemBuilder) {

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        super.onPostBindView(viewHolder, textColor, accentColor)
        //TODO add color circle view
        if (showPreview) {
            val preview = viewHolder.bindInnerView<CircleView>(R.layout.kau_preference_color_preview)
            preview.setBackgroundColor(pref)
            preview.withBorder = true
        }
    }


    override fun defaultOnClick(itemView: View, innerContent: View?): Boolean {
        itemView.context.colorPickerDialog {
            titleRes = this@KPrefColorPicker.title
            defaultColor = pref
            colorCallbacks.add {
                pref = it
                if (showPreview)
                    (innerContent as CircleView).setBackgroundColor(it)
            }
            applyNestedBuilder(colorBuilder)
        }.show()
        return true
    }

    override fun getType(): Int = R.id.kau_item_pref_color_picker

}