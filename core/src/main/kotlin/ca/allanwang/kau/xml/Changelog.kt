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
package ca.allanwang.kau.xml

import android.content.Context
import android.content.res.XmlResourceParser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.XmlRes
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.materialDialog
import ca.allanwang.kau.utils.use
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser

/**
 * Created by Allan Wang on 2017-05-28.
 *
 * Easy changelog loader
 */
fun Context.showChangelog(@XmlRes xmlRes: Int, @ColorInt textColor: Int? = null, customize: MaterialDialog.Builder.() -> Unit = {}) {
    val scope = this as? CoroutineScope ?: GlobalScope
    scope.launch {
        val items = async { parse(this@showChangelog, xmlRes) }.await()
        materialDialog {
            title(R.string.kau_changelog)
            positiveText(R.string.kau_great)
            adapter(ChangelogAdapter(items, textColor), null)
            customize()
        }
    }
}

/**
 * Internals of the changelog dialog
 * Contains an mainAdapter for each item, as well as the tags to parse
 */
internal class ChangelogAdapter(val items: List<Pair<String, ChangelogType>>, @ColorInt val textColor: Int? = null) :
    RecyclerView.Adapter<ChangelogAdapter.ChangelogVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChangelogVH(
        LayoutInflater.from(parent.context)
            .inflate(items[viewType].second.layout, parent, false)
    )

    override fun onBindViewHolder(holder: ChangelogVH, position: Int) {
        holder.text.text = items[position].first
        if (textColor != null) {
            holder.text.setTextColor(textColor)
            holder.bullet?.setTextColor(textColor)
        }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = items.size

    internal class ChangelogVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.kau_changelog_text)
        val bullet: TextView? = itemView.findViewById(R.id.kau_changelog_bullet)
    }
}

internal fun parse(context: Context, @XmlRes xmlRes: Int): List<Pair<String, ChangelogType>> {
    val items = mutableListOf<Pair<String, ChangelogType>>()
    context.resources.getXml(xmlRes).use { parser: XmlResourceParser ->
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG)
                ChangelogType.values.any { it.add(parser, items) }
            eventType = parser.next()
        }
    }
    return items
}

internal enum class ChangelogType(val tag: String, val attr: String, @LayoutRes val layout: Int) {
    TITLE("version", "title", R.layout.kau_changelog_title),
    ITEM("item", "text", R.layout.kau_changelog_content);

    companion object {
        val values = values()
    }

    /**
     * Returns true if tag matches; false otherwise
     */
    fun add(parser: XmlResourceParser, list: MutableList<Pair<String, ChangelogType>>): Boolean {
        if (parser.name != tag) return false
        if (parser.getAttributeValue(null, attr).isNotBlank())
            list.add(Pair(parser.getAttributeValue(null, attr), this))
        return true
    }
}
