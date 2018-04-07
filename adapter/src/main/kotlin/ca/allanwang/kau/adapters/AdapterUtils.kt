package ca.allanwang.kau.adapters

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.select.SelectExtension

/**
 * Created by Allan Wang on 2017-11-08.
 */

/**
 * Add kotlin's generic syntax to better support out types
 */
fun <Item : IItem<*, *>> fastAdapter(vararg adapter: IAdapter<out Item>) =
        FastAdapter.with<Item, IAdapter<out Item>>(adapter.toList())!!

inline fun <reified T : IAdapterExtension<Item>, Item : IItem<*, *>> FastAdapter<Item>.getExtension(): T? =
        getExtension(T::class.java)

/**
 * Returns selection size, or -1 if selection is disabled
 */
inline val <Item : IItem<*, *>> IAdapter<Item>.selectionSize: Int
    get() = fastAdapter.getExtension<SelectExtension<Item>, Item>()?.selections?.size ?: -1

inline val <Item : IItem<*, *>> IAdapter<Item>.selectedItems: Set<Item>
    get() = fastAdapter.getExtension<SelectExtension<Item>, Item>()?.selectedItems ?: emptySet()
