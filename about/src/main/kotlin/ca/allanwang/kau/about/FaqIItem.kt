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
package ca.allanwang.kau.about

import android.annotation.SuppressLint
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.colorToForeground
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.parentViewGroup
import ca.allanwang.kau.utils.setPaddingLeft
import ca.allanwang.kau.xml.FaqItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

/**
 * Created by Allan Wang on 2017-08-02.
 */
class FaqIItem(val content: FaqItem) : KauIItem<LibraryIItem, FaqIItem.ViewHolder>(
    R.layout.kau_iitem_faq, ::ViewHolder, R.id.kau_item_faq
), ThemableIItem by ThemableIItemDelegate() {

    companion object {
        fun bindEvents(fastAdapter: FastAdapter<IItem<*>>) {
            fastAdapter.withSelectable(false)
                .withEventHook(object : ClickEventHook<IItem<*>>() {

                    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? =
                        (viewHolder as? ViewHolder)?.questionContainer

                    override fun onClick(v: View, position: Int, adapter: FastAdapter<IItem<*>>, item: IItem<*>) {
                        if (item !is FaqIItem) return
                        item.isExpanded = !item.isExpanded
                        v.parentViewGroup.findViewById<CollapsibleTextView>(R.id.faq_item_answer)
                            .setExpanded(item.isExpanded)
                    }
                })
        }
    }

    private var isExpanded = false

    @SuppressLint("SetTextI18n")
    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        with(holder) {
            number.text = "${content.number}."
            question.text = content.question
            answer.setExpanded(isExpanded, false)
            if (accentColor != null) answer.setLinkTextColor(accentColor!!)
            answer.text = content.answer
            answer.post { answer.setPaddingLeft(16.dpToPx + number.width) }
            bindTextColor(number, question)
            bindTextColorSecondary(answer)
            val bg2 = backgroundColor?.colorToForeground(0.1f)
            if (bg2 != null)
                answer.setBackgroundColor(bg2)
            bindBackgroundRipple(questionContainer)
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            number.text = null
            question.text = null
            answer.text = null
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val container: ViewGroup = v.findViewById(R.id.faq_item)
        val questionContainer: ViewGroup = v.findViewById(R.id.faq_item_question_container)
        val number: TextView = v.findViewById(R.id.faq_item_number)
        val question: TextView = v.findViewById(R.id.faq_item_question)
        val answer: CollapsibleTextView = v.findViewById(R.id.faq_item_answer)

        init {
            answer.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}
