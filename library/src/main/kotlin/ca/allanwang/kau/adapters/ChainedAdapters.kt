package ca.allanwang.kau.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.logging.KL
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.HeaderAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import org.jetbrains.anko.collections.forEachReversedWithIndex
import java.util.*

/**
 * Created by Allan Wang on 2017-06-27.
 *
 * Once bounded to a [RecyclerView], this will
 * - Chain together a list of [HeaderAdapter]s, backed by a generic [FastItemAdapter]
 * - Add a [LinearLayoutManager] to the recycler
 * - Add a listener for when a new adapter segment is being used
 */
class ChainedAdapters<T>(vararg items: Pair<T, HeaderAdapter<*>>) {
    private val chain: MutableList<Pair<T, HeaderAdapter<*>>> = mutableListOf(*items)
    val baseAdapter: FastItemAdapter<IItem<*, *>> = FastItemAdapter()
    private val indexStack = Stack<Int>()
    var recycler: RecyclerView? = null
    val firstVisibleItemPosition: Int
        get() = (recycler?.layoutManager as LinearLayoutManager?)?.findFirstVisibleItemPosition() ?: throw IllegalArgumentException("No recyclerview was bounded to the chain adapters")

    fun add(vararg items: Pair<T, HeaderAdapter<*>>) = add(items.toList())

    fun add(items: Collection<Pair<T, HeaderAdapter<*>>>): ChainedAdapters<T> {
        if (recycler != null) throw IllegalAccessException("Chain adapter is already bounded to a recycler; cannot add directly.")
        chain.addAll(items)
        return this
    }

    operator fun get(index: Int) = chain[index]

    /**
     * Attaches the chain to a recycler
     * After this stage, any modifications to the adapters must be done through external references
     * You may still get the generic header adapters through the get operator
     * Binding the recycler also involves supplying a callback, which returns
     * the item (T) associated with the adapter,
     * the index (Int) of the current adapter
     * and the dy (Int) as given by the scroll listener
     */
    fun bindRecyclerView(recyclerView: RecyclerView, onAdapterSectionChanged: (item: T, index: Int, dy: Int) -> Unit) {
        if (recycler != null) throw IllegalStateException("Chain adapter is already bounded")
        if (chain.isEmpty()) throw IllegalArgumentException("No adapters have been added to the adapters list")
        //wrap adapters
        chain.map { it.second }.forEachReversedWithIndex { i, headerAdapter ->
            if (i == chain.size - 1) headerAdapter.wrap(baseAdapter)
            else headerAdapter.wrap(chain[i + 1].second)
            if (i < chain.size - 1) KL.e("${chain[i].first} ${chain[i + 1].first}")
        }
        recycler = recyclerView
        indexStack.push(0)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = chain.first().second
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(rv, dx, dy)
                    val topPosition = firstVisibleItemPosition
                    val currentAdapterIndex = indexStack.peek()
                    if (dy > 0) {
                        //look ahead from current adapter
                        val nextAdapterIndex = (currentAdapterIndex until chain.size).asSequence()
                                .firstOrNull {
                                    val adapter = chain[it].second
                                    KL.e("A $it ${adapter.adapterItemCount} ${adapter.getGlobalPosition(0)} $topPosition $currentAdapterIndex")
                                    adapter.adapterItemCount > 0 && adapter.getGlobalPosition(0) >= topPosition
                                } ?: currentAdapterIndex
                        KL.e("Next $nextAdapterIndex")
                        if (nextAdapterIndex == currentAdapterIndex) return
                        indexStack.push(nextAdapterIndex)
                        onAdapterSectionChanged(chain[indexStack.peek()].first, indexStack.peek(), dy)
                    } else if (currentAdapterIndex == 0) {
                        return //All adapters may be empty; in this case, if we are already at the beginning, don't bother checking
                    } else if (chain[currentAdapterIndex].second.getGlobalPosition(0) > topPosition) {
                        indexStack.pop()
                        onAdapterSectionChanged(chain[indexStack.peek()].first, indexStack.peek(), dy)
                    }
                }
            })
        }
    }
}