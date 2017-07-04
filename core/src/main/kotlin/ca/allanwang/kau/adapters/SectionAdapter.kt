package ca.allanwang.kau.adapters

import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.HeaderAdapter

/**
 * Created by Allan Wang on 2017-06-27.
 *
 * Extension of [HeaderAdapter] where we can define the order
 */
class SectionAdapter<Item : IItem<*, *>>(var sectionOrder: Int = 100) : HeaderAdapter<Item>() {
    override fun getOrder(): Int = sectionOrder
}