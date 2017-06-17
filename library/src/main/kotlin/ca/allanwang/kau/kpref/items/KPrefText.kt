package ca.allanwang.kau.kpref.items

import android.support.annotation.StringRes
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import ca.allanwang.kau.utils.toast

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Text preference
 * Holds a textview to display data on the right
 * This is still a generic preference
 *
 */
class KPrefText<T>(adapterBuilder: KPrefAdapterBuilder,
                   @StringRes title: Int,
                   val builder: KPrefTextContract<T>
) : KPrefItemBase<T>(adapterBuilder, title, builder) {

    override fun defaultOnClick(itemView: View, innerContent: View?): Boolean {
        itemView.context.toast("No click function set")
        return true
    }

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        super.onPostBindView(viewHolder, textColor, accentColor)
        val textview = viewHolder.bindInnerView<TextView>(R.layout.kau_preference_text)
        if (textColor != null) textview.setTextColor(textColor)
        textview.text = builder.textGetter.invoke(pref)
    }

    class KPrefTextBuilder<T> : KPrefTextContract<T>, BaseContract<T> by BaseBuilder<T>() {
        override var textGetter: (T) -> String? = { it?.toString() }
    }

    interface KPrefTextContract<T> : BaseContract<T> {
        var textGetter: (T) -> String?
    }

    override fun getType(): Int = R.id.kau_item_pref_checkbox

}