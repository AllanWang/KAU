/*
 * Copyright 2019 Allan Wang
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
