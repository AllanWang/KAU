package ca.allanwang.kau.kpref.items

import android.support.annotation.StringRes
import android.view.View
import android.widget.CheckBox
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import ca.allanwang.kau.logging.SL
import ca.allanwang.kau.utils.tint
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Checkbox preference
 * When clicked, will toggle the preference and the apply the result to the checkbox
 */
class KPrefCheckbox(builder: KPrefAdapterBuilder,
                    @StringRes title: Int,
                    @StringRes description: Int = -1,
                    iicon: IIcon? = null,
                    enabled: Boolean = true,
                    getter: () -> Boolean,
                    setter: (value: Boolean) -> Unit) : KPrefItemBase<Boolean>(builder, title, description, iicon, enabled, getter, setter) {


    override fun onClick(itemView: View): Boolean {
        val checkbox = itemView.findViewById(R.id.kau_pref_checkbox) as CheckBox
        pref = !pref
        checkbox.isChecked = pref
        return true
    }

    override fun onPostBindView(viewHolder: ViewHolder, builder: KPrefAdapterBuilder) {
        super.onPostBindView(viewHolder, builder)
        viewHolder.addInnerView(R.layout.kau_preference_checkbox)
        if (builder.accentColor != null) {
            val checkbox = viewHolder.itemView.findViewById(R.id.kau_pref_checkbox) as CheckBox
            checkbox.tint(builder.accentColor!!)
            checkbox.isChecked = pref //Checkbox tick needs to be delayed since notifyDataSetChanged will cancel the animation
            //It seems to work well here
        }
    }

    override fun getType(): Int = R.id.kau_item_pref_checkbox

}