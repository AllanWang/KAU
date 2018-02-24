package ca.allanwang.kau.adapters

import android.support.annotation.IntRange
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.OnClickListener

/**
 * Created by Allan Wang on 26/12/17.
 */
fun <Item : IItem<*, *>> FastAdapter<Item>.withOnRepeatedClickListener(count: Int,
                                                                       duration: Long,
                                                                       event: OnClickListener<Item>) =
        withOnClickListener(RepeatedClickListener(count, duration, event))

/**
 * Registers and skips each click until the designated [count] clicks are triggered,
 * each within [duration] from each other.
 * Only then will the [event] be fired, and everything will be reset.
 */
private class RepeatedClickListener<Item : IItem<*, *>>(
        @IntRange(from = 1) val count: Int,
        @IntRange(from = 1) val duration: Long,
        val event: OnClickListener<Item>) : OnClickListener<Item> {

    init {
        if (count <= 0)
            throw IllegalArgumentException("RepeatedClickListener's count must be > 1")
        if (duration <= 0)
            throw IllegalArgumentException("RepeatedClickListener's duration must be > 1L")
    }

    private var chain = 0
    private var time = -1L

    override fun onClick(v: View?, adapter: IAdapter<Item>, item: Item, position: Int): Boolean {
        val now = System.currentTimeMillis()
        if (time - now < duration)
            chain++
        else
            chain = 1
        time = now
        if (chain == count) {
            chain = 0
            event.onClick(v, adapter, item, position)
            return true
        }
        return false
    }
}