package ca.allanwang.kau.iitems

import android.annotation.SuppressLint
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.mikepenz.fastadapter.IClickable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * Created by Allan Wang on 2017-07-03.
 *
 * Kotlin implementation of the [AbstractItem] to make things shorter
 * If only one iitem type extends the given [layoutRes], you may use it as the type and not worry about another id
 */
open class KauIItem<Item, VH : RecyclerView.ViewHolder>(
        @param:LayoutRes private val layoutRes: Int,
        private val viewHolder: (v: View) -> VH,
        private val type: Int = layoutRes
) : AbstractItem<Item, VH>() where Item : IItem<*, *>, Item : IClickable<*> {
    @SuppressLint("ResourceType")
    final override fun getType(): Int = type

    final override fun getViewHolder(v: View): VH = viewHolder(v)
    final override fun getLayoutRes(): Int = layoutRes
}