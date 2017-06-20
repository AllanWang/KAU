package com.pitchedapps.frost.utils

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.annotation.LayoutRes
import android.support.annotation.XmlRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.allanwang.kau.R
import org.xmlpull.v1.XmlPullParser


/**
 * Created by Allan Wang on 2017-05-28.
 */
internal class ChangelogAdapter(val items: List<Pair<String, ChangelogType>>) : RecyclerView.Adapter<ChangelogAdapter.ChangelogVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChangelogVH(LayoutInflater.from(parent.context)
            .inflate(items[viewType].second.layout, parent, false))

    override fun onBindViewHolder(holder: ChangelogVH, position: Int) {
        holder.text.text = items[position].first
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    override fun getItemCount() = items.size

    internal class ChangelogVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById<TextView>(R.id.kau_changelog_text)
    }
}

internal fun parse(context: Context, @XmlRes xmlRes: Int): List<Pair<String, ChangelogType>> {
    val items = mutableListOf<Pair<String, ChangelogType>>()
    context.resources.getXml(xmlRes).use {
        parser ->
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
    TITLE("title", "version", R.layout.kau_changelog_title),
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

