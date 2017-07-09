package ca.allanwang.kau.iitems

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.adapter.R
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.string
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Simple Header with lots of padding on the top
 * Contains only one text view
 */
class HeaderIItem(text: String? = null, var textRes: Int = -1
) : AbstractItem<HeaderIItem, HeaderIItem.ViewHolder>(), ThemableIItem by ThemableIItemDelegate() {

    var text: String = text ?: "Header Placeholder"

    override fun getType(): Int = R.id.kau_item_header_big_margin_top

    override fun getLayoutRes(): Int = R.layout.kau_iitem_header

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder.text.text = holder.itemView.context.string(textRes, text)
        bindTextColor(holder.text)
        bindBackgroundColor(holder.container)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.text.text = null
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView by bindView(R.id.kau_header_text)
        val container: CardView by bindView(R.id.kau_header_container)
    }

}