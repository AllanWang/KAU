package ca.allanwang.kau.adapters

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil

/**
 * Fast adapter with prewrapped item adapter
 */
class SingleFastAdapter private constructor(val adapter: ItemAdapter<GenericItem>) :
    FastAdapter<GenericItem>(), IItemAdapter<GenericItem, GenericItem> by adapter {

    constructor() : this(ItemAdapter())

    var lastClearTime: Long = -1

    init {
        super.addAdapter(0, adapter)
    }

    override fun clear(): SingleFastAdapter {
        if (itemCount != 0) {
            adapter.clear()
            lastClearTime = System.currentTimeMillis()
        }
        return this
    }

    override fun <A : IAdapter<GenericItem>> addAdapter(
        index: Int,
        adapter: A
    ): FastAdapter<GenericItem> {
        throw IllegalStateException("SingleFastAdapter only allows one adapter.")
    }

    fun setWithDiff(items: List<GenericItem>, detectMoves: Boolean = true) {
        FastAdapterDiffUtil.set(adapter, items, detectMoves)
    }
}