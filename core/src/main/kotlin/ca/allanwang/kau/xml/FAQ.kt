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
import android.text.Html
import android.text.Spanned
import androidx.annotation.XmlRes
import ca.allanwang.kau.utils.use
import org.xmlpull.v1.XmlPullParser

/** Created by Allan Wang on 2017-07-30. */

/**
 * Parse an xml asynchronously with two tags, <question>Text</question> and <answer>Text</answer>.
 * Note that this should executed in a background thread.
 */
@Suppress("DEPRECATION")
fun Context.kauParseFaq(
  @XmlRes xmlRes: Int,
  /** If \n is used, it will automatically be converted to </br> */
  parseNewLine: Boolean = true
): List<FaqItem> {
  val items = mutableListOf<FaqItem>()
  resources.getXml(xmlRes).use { parser: XmlResourceParser ->
    var eventType = parser.eventType
    var question: Spanned? = null
    var flag = -1 // -1, 0, 1 -> invalid, question, answer
    while (eventType != XmlPullParser.END_DOCUMENT) {
      if (eventType == XmlPullParser.START_TAG) {
        flag =
          when (parser.name) {
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
            items.add(
              FaqItem(
                items.size + 1,
                question
                  ?: throw IllegalArgumentException("KAU FAQ answer found without a question"),
                Html.fromHtml(parser.text.replace("\n", if (parseNewLine) "<br/>" else ""))
              )
            )
            question = null
            flag = -1
          }
        }
      }
      eventType = parser.next()
    }
  }
  return items
}

data class FaqItem(val number: Int, val question: Spanned, val answer: Spanned)
