package ca.allanwang.kau.kpref.items

import android.support.annotation.StringRes
import android.view.View
import ca.allanwang.kau.R

/**
 * Created by Allan Wang on 2017-06-07.
 */
class KPrefHeader(@StringRes title: Int) : KPrefItemCore(title = title) {

    override fun getLayoutRes(): Int = R.layout.kau_preference_header

    override fun onPostBindView(viewHolder: ViewHolder) {
        viewHolder.itemView.isClickable = false
    }

    override fun onClick(itemView: View): Boolean = true

    override fun getType() = R.id.kau_item_pref_header

}