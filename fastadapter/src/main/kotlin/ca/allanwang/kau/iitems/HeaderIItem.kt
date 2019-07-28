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

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.adapter.R
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.utils.INVALID_ID
import ca.allanwang.kau.utils.string

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Simple Header with lots of padding on the top
 * Contains only one text view
 */
class HeaderIItem(
    text: String? = null,
    var textRes: Int = INVALID_ID
) : KauIItem<HeaderIItem.ViewHolder>(
    R.layout.kau_iitem_header, { ViewHolder(it) }, R.id.kau_item_header_big_margin_top
), ThemableIItem by ThemableIItemDelegate() {

    var text: String = text ?: "Header Placeholder"

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.text.text = holder.itemView.context.string(textRes, text)
        bindTextColor(holder.text)
        bindBackgroundColor(holder.container)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.text.text = null
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.kau_header_text)
        val container: CardView = v.findViewById(R.id.kau_header_container)
    }
}
