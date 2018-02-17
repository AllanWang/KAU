package ca.allanwang.kau.adapters

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem

/**
 * Created by Allan Wang on 2017-11-08.
 */

/**
 * Add kotlin's generic syntax to better support out types
 */
fun <Item : IItem<*, *>> fastAdapter(vararg adapter: IAdapter<out Item>) =
        FastAdapter.with<Item, IAdapter<out Item>>(adapter.toList())!!