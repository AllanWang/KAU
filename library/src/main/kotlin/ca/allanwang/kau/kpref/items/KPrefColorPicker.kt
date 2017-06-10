package ca.allanwang.kau.kpref.items

import android.support.annotation.StringRes
import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.dialogs.color.colorPickerDialog
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-07.
 */
class KPrefColorPicker(@StringRes title: Int,
                       @StringRes description: Int = -1,
                       iicon: IIcon? = null,
                       enabled: Boolean = true,
                       getter: () -> Int,
                       setter: (value: Int) -> Unit) : KPrefItemBase<Int>(title, description, iicon, enabled, getter, setter) {

    override fun onPostBindView(viewHolder: KPrefItemCore.ViewHolder) {
        super.onPostBindView(viewHolder)
        //TODO add color circle view
    }

    override fun onClick(itemView: View): Boolean {
        itemView.context.colorPickerDialog {
            titleRes = this@KPrefColorPicker.title
            defaultColor = pref
            colorCallbacks.add { pref = it }
        }.show()
        return true
    }

    override fun getType(): Int = R.id.kau_item_pref_color_picker

}