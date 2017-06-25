package ca.allanwang.kau.searchview

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.setIcon
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-23.
 *
 * A holder for each individual search item
 * Contains a [key] which acts as a unique identifier (eg url)
 * and a [content] which is displayed in the item
 */
class SearchItem(val key: String, val content: String = key, val iicon: IIcon? = GoogleMaterial.Icon.gmd_search
) : AbstractItem<SearchItem, SearchItem.ViewHolder>() {

    companion object {
        var foregroundColor: Int? = null
    }

    override fun getLayoutRes(): Int = R.layout.kau_search_item

    override fun getType(): Int = R.id.kau_item_search

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        if (foregroundColor != null) {
            holder.text.setTextColor(foregroundColor!!)
            holder.icon.setIcon(iicon, sizeDp = 18, color = foregroundColor!!)
        }
        holder.text.text = content
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.text.text = null
        holder.icon.setImageDrawable(null)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView by bindView(R.id.search_icon)
        val text: TextView by bindView(R.id.search_text)
    }
}