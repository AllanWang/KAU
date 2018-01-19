package ca.allanwang.kau.kpref.activity.items

import android.support.v7.widget.AppCompatCheckBox
import android.widget.CheckBox
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.R
import ca.allanwang.kau.utils.tint

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Checkbox preference
 * When clicked, will toggle the preference and the apply the result to the checkbox
 */
open class KPrefCheckbox(builder: BaseContract<Boolean>) : KPrefItemBase<Boolean>(builder) {

    override fun KClick<Boolean>.defaultOnClick() {
        pref = !pref
        (innerView as AppCompatCheckBox).isChecked = pref
    }

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        super.onPostBindView(viewHolder, textColor, accentColor)
        val checkbox = viewHolder.bindInnerView<CheckBox>(R.layout.kau_pref_checkbox)
        if (accentColor != null) checkbox.tint(accentColor)
        checkbox.isChecked = pref
        checkbox.jumpDrawablesToCurrentState() //Cancel the animation
    }

    override fun getType(): Int = R.id.kau_item_pref_checkbox

}