package ca.allanwang.kau.kpref.activity.items

import androidx.appcompat.widget.AppCompatCheckBox
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

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        val checkbox = holder.bindInnerView<CheckBox>(R.layout.kau_pref_checkbox)
        withAccentColor(checkbox::tint)
        checkbox.isChecked = pref
        checkbox.jumpDrawablesToCurrentState() //Cancel the animation
    }

    override fun getType(): Int = R.id.kau_item_pref_checkbox

}