package ca.allanwang.kau.kpref.items

import android.support.annotation.StringRes
import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.dialogs.color.Builder
import ca.allanwang.kau.dialogs.color.colorPickerDialog
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * ColorPicker preference
 * When a color is successfully selected in the dialog, it will be saved as an int
 */
class KPrefColorPicker(builder: KPrefAdapterBuilder,
                       @StringRes title: Int,
                       @StringRes description: Int = -1,
                       iicon: IIcon? = null,
                       enabler: () -> Boolean = { true },
                       getter: () -> Int,
                       setter: (value: Int) -> Unit,
                       val configs: Builder.() -> Unit = {}) : KPrefItemBase<Int>(builder, title, description, iicon, enabler, getter, setter) {

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        super.onPostBindView(viewHolder, textColor, accentColor)
        //TODO add color circle view
    }


    override fun onClick(itemView: View, innerContent: View?): Boolean {
        itemView.context.colorPickerDialog {
            titleRes = this@KPrefColorPicker.title
            defaultColor = pref
            colorCallbacks.add { pref = it }
            applyNestedBuilder(configs)
        }.show()
        return true
    }

    override fun getType(): Int = R.id.kau_item_pref_color_picker

}