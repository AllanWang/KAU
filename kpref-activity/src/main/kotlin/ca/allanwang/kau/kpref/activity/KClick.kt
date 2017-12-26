package ca.allanwang.kau.kpref.activity

import android.content.Context
import android.view.View
import ca.allanwang.kau.kpref.activity.items.KPrefItemBase

/**
 * Created by Allan Wang on 10/12/17.
 */
interface KClick<T> {

    val context: Context
    /**
     * Base view container from ViewHolder
     */
    val itemView: View

    /**
     * Optional inner view which differs per element
     */
    val innerView: View?

    /**
     * The item holding the data
     */
    val item: KPrefItemBase<T>
}