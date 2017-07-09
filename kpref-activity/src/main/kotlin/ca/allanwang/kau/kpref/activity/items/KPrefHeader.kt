package ca.allanwang.kau.kpref.activity.items

import android.view.View
import ca.allanwang.kau.kpref.activity.R

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Header preference
 * This view just holds a title and is not clickable. It is styled using the accent color
 */
open class KPrefHeader(builder: CoreContract) : KPrefItemCore(builder) {

    override fun getLayoutRes(): Int = R.layout.kau_preference_header

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        if (accentColor != null) viewHolder.title.setTextColor(accentColor)
    }

    override fun onClick(itemView: View, innerContent: View?): Boolean = true

    override fun getType() = R.id.kau_item_pref_header

}