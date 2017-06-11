package ca.allanwang.kau.kpref.items

import android.support.annotation.StringRes
import android.view.View
import android.widget.CheckBox
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import ca.allanwang.kau.utils.tint
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-07.
 */
class KPrefCheckbox(builder: KPrefAdapterBuilder,
                    @StringRes title: Int,
                    @StringRes description: Int = -1,
                    iicon: IIcon? = null,
                    enabled: Boolean = true,
                    getter: () -> Boolean,
                    setter: (value: Boolean) -> Unit) : KPrefItemBase<Boolean>(builder, title, description, iicon, enabled, getter, setter) {

    override fun onPostBindView(viewHolder: KPrefItemCore.ViewHolder) {
        super.onPostBindView(viewHolder)
        viewHolder.addInnerView(R.layout.kau_preference_checkbox)
        (viewHolder[R.id.kau_pref_checkbox] as CheckBox).isChecked = pref
    }

    override fun onClick(itemView: View): Boolean {
        val checkbox = itemView.findViewById(R.id.kau_pref_checkbox) as CheckBox
        pref = !pref
        checkbox.isChecked = pref
        return true
    }

    override fun setColors(viewHolder: ViewHolder, builder: KPrefAdapterBuilder) {
        super.setColors(viewHolder, builder)
        if (builder.accentColor != null) {
            val checkbox = viewHolder.itemView.findViewById(R.id.kau_pref_checkbox) as CheckBox
            checkbox.tint(builder.accentColor!!)
        }
    }

    override fun getType(): Int = R.id.kau_item_pref_checkbox

}