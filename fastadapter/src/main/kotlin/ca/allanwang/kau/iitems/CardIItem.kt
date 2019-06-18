/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.iitems

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.adapter.R
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.utils.INVALID_ID
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.toDrawable
import ca.allanwang.kau.utils.visible
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Simple generic card item with an icon, title, description and button
 * The icon and button are hidden by default unless values are given
 */
class CardIItem(
    val builder: Config.() -> Unit = {}
) : KauIItem<CardIItem, CardIItem.ViewHolder>(
    R.layout.kau_iitem_card, ::ViewHolder, R.id.kau_item_card
), ThemableIItem by ThemableIItemDelegate() {

    companion object {
        fun bindClickEvents(fastAdapter: FastAdapter<IItem<*, *>>) {
            fastAdapter.withEventHook(object : ClickEventHook<IItem<*, *>>() {
                override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                    return if (viewHolder is ViewHolder) listOf(viewHolder.card, viewHolder.button) else null
                }

                override fun onClick(v: View, position: Int, adapter: FastAdapter<IItem<*, *>>, item: IItem<*, *>) {
                    if (item !is CardIItem) return
                    with(item.configs) {
                        when (v.id) {
                            R.id.kau_card_container -> cardClick?.invoke()
                            R.id.kau_card_button -> buttonClick?.invoke()
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
        var titleRes: Int = INVALID_ID
        var desc: String? = null
        var descRes: Int = INVALID_ID
        var button: String? = null
        var buttonRes: Int = INVALID_ID
        var buttonClick: (() -> Unit)? = null
        var cardClick: (() -> Unit)? = null
        var image: Drawable? = null
        var imageIIcon: IIcon? = null
        var imageIIconColor: Int = Color.WHITE
        var imageRes: Int = INVALID_ID
    }

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        with(holder.itemView.context) context@{
            with(configs) {
                holder.title.text = string(titleRes, title)
                val descText = string(descRes, desc)
                if (descText != null) holder.description.visible().text = descText
                val buttonText = string(buttonRes, button)
                if (buttonText != null) {
                    holder.bottomRow.visible()
                    holder.button.text = buttonText
                }
                val icon = drawable(imageRes) {
                    imageIIcon?.toDrawable(this@context, sizeDp = 24, color = imageIIconColor)
                        ?: image
                }
                if (icon != null) holder.icon.visible().setImageDrawable(icon)
            }
            with(holder) {
                bindTextColor(title)
                bindTextColorSecondary(description)
                bindAccentColor(button)
                if (configs.imageIIcon != null) bindIconColor(icon)
                bindBackgroundRipple(card)
            }
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            icon.gone().setImageDrawable(null)
            title.text = null
            description.gone().text = null
            bottomRow.gone()
            button.setOnClickListener(null)
            card.setOnClickListener(null)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView = v.findViewById(R.id.kau_card_container)
        val icon: ImageView = v.findViewById(R.id.kau_card_image)
        val title: TextView = v.findViewById(R.id.kau_card_title)
        val description: TextView = v.findViewById(R.id.kau_card_description)
        val bottomRow: LinearLayout = v.findViewById(R.id.kau_card_bottom_row)
        val button: Button = v.findViewById(R.id.kau_card_button)
    }
}
