package ca.allanwang.kau.kpref.items

import android.support.annotation.StringRes
import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.KPrefAdapterBuilder

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Header preference
 * This view just holds a title and is not clickable. It is styled using the accent color
 */
class KPrefHeader(builder: KPrefAdapterBuilder, @StringRes title: Int) : KPrefItemCore(builder, title = title) {

    override fun getLayoutRes(): Int = R.layout.kau_preference_header

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        viewHolder.itemView.isClickable = false
        if (accentColor != null) viewHolder.title.setTextColor(accentColor)
    }

    override fun onClick(itemView: View, innerContent: View?): Boolean = true

    override fun getType() = R.id.kau_item_pref_header

}