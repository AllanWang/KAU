package ca.allanwang.kau.xml

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.annotation.XmlRes
import android.text.Html
import android.text.Spanned
import ca.allanwang.kau.utils.use
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParser

/**
 * Created by Allan Wang on 2017-07-30.
 */

/**
 * Parse an xml asynchronously with two tags, <question>Text</question> and <answer>Text</answer>,
 * and invoke the [callback] on the ui thread
 */
@Suppress("DEPRECATION")
fun Context.kauParseFaq(
        @XmlRes xmlRes: Int,
        /**
         * If \n is used, it will automatically be converted to </br>
         */
        parseNewLine: Boolean = true,
        callback: (items: List<FaqItem>) -> Unit) {
    doAsync {
        val items = mutableListOf<FaqItem>()
        resources.getXml(xmlRes).use { parser: XmlResourceParser ->
            var eventType = parser.eventType
            var question: Spanned? = null
            var flag = -1 //-1, 0, 1 -> invalid, question, answer
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    flag = when (parser.name) {
                        "question" -> 0
                        "answer" -> 1
                        else -> -1
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    when (flag) {
                        0 -> {
                            question = Html.fromHtml(parser.text.replace("\n", if (parseNewLine) "<br/>" else ""))
                            flag = -1
                        }
                        1 -> {
                            items.add(FaqItem(items.size + 1,
                                    question ?: throw IllegalArgumentException("KAU FAQ answer found without a question"),
                                    Html.fromHtml(parser.text.replace("\n", if (parseNewLine) "<br/>" else ""))))
                            question = null
                            flag = -1
                        }
                    }
                }
                eventType = parser.next()
            }
        }
        uiThread { callback(items) }
    }
}

data class FaqItem(val number: Int, val question: Spanned, val answer: Spanned)