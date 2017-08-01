package ca.allanwang.kau.xml

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.annotation.XmlRes
import android.text.Html
import android.text.Spanned
import ca.allanwang.kau.utils.use
import org.xmlpull.v1.XmlPullParser

/**
 * Created by Allan Wang on 2017-07-30.
 */

/**
 * Parse an xml with two tags, <question>Text</question> and <answer>Text</answer>,
 * and return them as a list of string pairs
 */
fun Context.kauParseFaq(@XmlRes xmlRes: Int, withNumbering: Boolean = true): List<Pair<Spanned, Spanned>> {
    val items = mutableListOf<Pair<Spanned, Spanned>>()
    resources.getXml(xmlRes).use {
        parser: XmlResourceParser ->
        var eventType = parser.eventType
        var question: Spanned? = null
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.name == "question") {
                    var q = parser.text.replace("\n", "<br/>")
                    if (withNumbering) q = "${items.size + 1}. $q"
                    question = Html.fromHtml(q)
                } else if (parser.name == "answer") {
                    items.add(Pair(question ?: throw IllegalArgumentException("KAU FAQ answer found without a question"),
                            Html.fromHtml(parser.text.replace("\n", "<br/>"))))
                    question = null
                }
            }

            eventType = parser.next()
        }
    }
    return items
}