package ca.allanwang.kau.iitems

import android.os.Build
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.R
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.visible
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

/**
 * Created by Allan Wang on 2017-06-27.
 */
class LibraryIItem(val lib: Library
) : AbstractItem<LibraryIItem, LibraryIItem.ViewHolder>(), ThemableIItem by ThemableIItemDelegate() {

    companion object {
        @JvmStatic fun bindClickEvents(fastAdapter: FastAdapter<CardIItem>) {
            fastAdapter.withEventHook(object : ClickEventHook<CardIItem>() {
                override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                    return if (viewHolder is CardIItem.ViewHolder) listOf(viewHolder.card, viewHolder.button) else null
                }

                override fun onClick(v: View, position: Int, adapter: FastAdapter<CardIItem>, item: CardIItem) {
                    with(item.configs) {
                        when (v.id) {
                            R.id.kau_card_container -> cardClick?.onClick(v)
                            R.id.kau_card_button -> buttonClick?.onClick(v)
                            else -> {
                            }
                        }
                    }
                }
            })
        }
    }

    override fun getType(): Int = R.id.kau_item_library

    override fun getLayoutRes(): Int = R.layout.kau_iitem_library

    override fun isSelectable(): Boolean = false

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        with(holder) {
            name.text = lib.libraryName
            creator.text = lib.author
            description.text = if (lib.libraryDescription.isBlank()) lib.libraryDescription
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                Html.fromHtml(lib.libraryDescription, Html.FROM_HTML_MODE_LEGACY)
            else Html.fromHtml(lib.libraryDescription)
            bottomDivider.gone()
            if (lib.libraryVersion?.isNotBlank() ?: false) {
                bottomDivider.visible()
                version.visible().text = lib.libraryVersion
            }
            if (lib.license?.licenseName?.isNotBlank() ?: false) {
                bottomDivider.visible()
                license.visible().text = lib.license?.licenseName
            }
            bindTextColor(name, creator)
            bindTextColorSecondary(description)
            bindAccentColor(license, version)
            bindDividerColor(divider, bottomDivider)
            bindBackgroundColor(card)
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            name.text = null
            creator.text = null
            description.text = null
            bottomDivider.gone()
            version.gone().text = null
            license.gone().text = null
        }
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView by bindView(R.id.lib_item_card)
        val name: TextView by bindView(R.id.lib_item_name)
        val creator: TextView by bindView(R.id.lib_item_author)
        val description: TextView by bindView(R.id.lib_item_description)
        val version: TextView by bindView(R.id.lib_item_version)
        val license: TextView by bindView(R.id.lib_item_license)
        val divider: View by bindView(R.id.lib_item_top_divider)
        val bottomDivider: View by bindView(R.id.lib_item_bottom_divider)
    }

}