package ca.allanwang.kau.kpref.items

import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.GlobalOptions

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Just text with the core options. Extends base preference but has an empty getter and setter
 *
 */
class KPrefPlainText(val builder: KPrefPlainTextBuilder) : KPrefItemBase<Unit>(builder) {

    override fun defaultOnClick(itemView: View, innerContent: View?): Boolean {
        //nothing
        return true
    }

    class KPrefPlainTextBuilder(
            globalOptions: GlobalOptions,
            titleRes: Int
    ) : BaseContract<Unit> by BaseBuilder(globalOptions, titleRes, {}, {})

    override fun getType(): Int = R.id.kau_item_pref_plain_text

}