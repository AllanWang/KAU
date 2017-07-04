package ca.allanwang.kau.iitems

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mikepenz.fastadapter.IClickable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * Created by Allan Wang on 2017-07-03.
 *
 * Kotlin implementation of the [AbstractItem] to make things shorter
 */
open class KauIItem<Item, VH : RecyclerView.ViewHolder>(
        private val type: Int,
        @param:LayoutRes private val layoutRes: Int,
        private val viewHolder: (v: View) -> VH
) : AbstractItem<Item, VH>() where Item : IItem<*, *>, Item : IClickable<*> {
    override final fun getType(): Int = type
    override final fun getViewHolder(v: View): VH = viewHolder(v)
    override final fun getLayoutRes(): Int = layoutRes
}