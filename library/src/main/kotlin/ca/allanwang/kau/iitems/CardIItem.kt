package ca.allanwang.kau.iitems

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Simple generic card item with an icon, title, description and button
 * The icon and button are hidden by default unless values are given
 */
class CardIItem(val builder: Config.() -> Unit = {}) : AbstractItem<CardIItem, CardIItem.ViewHolder>() {


    companion object {
        fun bindClickEvents(fastAdapter: FastAdapter<CardIItem>) {
            fastAdapter.withEventHook(object : ClickEventHook<CardIItem>() {
                override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                    return if (viewHolder is ViewHolder) listOf(viewHolder.card, viewHolder.button) else null
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

    val configs = Config().apply { builder() }

    class Config {
        var title: String? = null
        var titleRes: Int = -1
        var desc: String? = null
        var descRes: Int = -1
        var button: String? = null
        var buttonRes: Int = -1
        var buttonClick: View.OnClickListener? = null
        var cardClick: View.OnClickListener? = null
        var image: Drawable? = null
        var imageIIcon: IIcon? = null
        var imageIIconColor: Int = Color.WHITE
        var imageRes: Int = -1
    }


    override fun getType(): Int = R.id.kau_item_card

    override fun getLayoutRes(): Int = R.layout.kau_iitem_card

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        with(holder.itemView.context) context@ {
            with(configs) {
                holder.title.text = string(titleRes, title)
                holder.description.text = string(descRes, desc)
                val buttonText = string(buttonRes, button)
                if (buttonText != null) {
                    holder.bottomRow.visible()
                    holder.button.text = buttonText
                    holder.button.setOnClickListener(buttonClick)
                }
                holder.icon.setImageDrawable(
                        if (imageRes > 0) drawable(imageRes)
                        else if (imageIIcon != null) imageIIcon!!.toDrawable(this@context, sizeDp = 40, color = imageIIconColor)
                        else image
                )
                holder.card.setOnClickListener(cardClick)
            }
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            icon.gone().setImageDrawable(null)
            title.text = null
            description.text = null
            bottomRow.gone()
            button.setOnClickListener(null)
            card.setOnClickListener(null)
        }
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView by bindView(R.id.kau_card_container)
        val icon: ImageView by bindView(R.id.kau_card_image)
        val title: TextView by bindView(R.id.kau_card_title)
        val description: TextView by bindView(R.id.kau_card_description)
        val bottomRow: LinearLayout by bindView(R.id.kau_card_bottom_row)
        val button: Button by bindView(R.id.kau_card_button)
    }

}