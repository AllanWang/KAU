package ca.allanwang.kau.adapters

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem

/**
 * Created by Allan Wang on 2017-11-08.
 */
fun fastAdapter(vararg adapter: IAdapter<*>) =
        FastAdapter.with<IItem<*, *>, IAdapter<out IItem<*, *>>>(adapter.toList())!!