package ca.allanwang.kau.about

import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.bindView
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
        R.layout.kau_iitem_faq, { ViewHolder(it) }, R.id.kau_item_faq
), ThemableIItem by ThemableIItemDelegate() {

    companion object {


        @JvmStatic fun bindEvents(fastAdapter: FastAdapter<IItem<*, *>>) {
            fastAdapter.withSelectable(false)
                    .withEventHook(object : ClickEventHook<IItem<*, *>>() {

                        override fun onBind(viewHolder: RecyclerView.ViewHolder): View?
                                = (viewHolder as? ViewHolder)?.questionContainer

                        override fun onClick(v: View, position: Int, adapter: FastAdapter<IItem<*, *>>, item: IItem<*, *>) {
                            if (item !is FaqIItem) return
                            item.isExpanded = !item.isExpanded
                            v.parentViewGroup.findViewById<CollapsibleTextView>(R.id.faq_item_answer).setExpanded(item.isExpanded)
                        }

                    })
        }
    }

    private var isExpanded = false

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
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
            bindDividerColor(answer)
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
        val container: ViewGroup by bindView(R.id.faq_item)
        val questionContainer: ViewGroup by bindView(R.id.faq_item_question_container)
        val number: TextView by bindView(R.id.faq_item_number)
        val question: TextView by bindView(R.id.faq_item_question)
        val answer: CollapsibleTextView by bindView(R.id.faq_item_answer)

        init {
            answer.movementMethod = LinkMovementMethod.getInstance()
        }
    }

}