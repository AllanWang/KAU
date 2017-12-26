package ca.allanwang.kau.adapters

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-11-08.
 */

/**
 * Add kotlin's generic syntax to better support out types
 */
fun <T : IItem<*, *>> fastAdapter(vararg adapter: IAdapter<out T>) =
        FastAdapter.with<T, IAdapter<out T>>(adapter.toList())!!