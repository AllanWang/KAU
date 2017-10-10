package ca.allanwang.kau.searchview

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-23.
 *
 * A holder for each individual search item
 * Contains a [key] which acts as a unique identifier (eg url)
 * and a [content] which is displayed in the item
 */
class SearchItem(val key: String,
                 val content: String = key,
                 val description: String? = null,
                 val iicon: IIcon? = GoogleMaterial.Icon.gmd_search,
                 val image: Drawable? = null
) : KauIItem<SearchItem, SearchItem.ViewHolder>(
        R.layout.kau_search_iitem,
        { ViewHolder(it) },
        R.id.kau_item_search
) {

    companion object {
        var foregroundColor: Int = 0xdd000000.toInt()
        var backgroundColor: Int = 0xfffafafa.toInt()
    }

    var styledContent: SpannableStringBuilder? = null

    /**
     * Highlight the subText if it is present in the content
     */
    fun withHighlights(subText: String) {
        val index = content.indexOf(subText)
        if (index == -1) return
        styledContent = SpannableStringBuilder(content)
        styledContent!!.setSpan(StyleSpan(Typeface.BOLD), index, index + subText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder.title.setTextColor(foregroundColor)
        holder.desc.setTextColor(foregroundColor.adjustAlpha(0.6f))

        if (image != null) holder.icon.setImageDrawable(image)
        else holder.icon.setIcon(iicon, sizeDp = 18, color = foregroundColor)

        holder.container.setRippleBackground(foregroundColor, backgroundColor)
        holder.title.text = styledContent ?: content
        if (description?.isNotBlank() ?: false) holder.desc.visible().text = description
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.title.text = null
        holder.desc.gone().text = null
        holder.icon.setImageDrawable(null)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView by bindView(R.id.kau_search_icon)
        val title: TextView by bindView(R.id.kau_search_title)
        val desc: TextView by bindView(R.id.kau_search_desc)
        val container: ConstraintLayout by bindView(R.id.kau_search_item_frame)
    }
}