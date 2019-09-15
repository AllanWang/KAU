package ca.allanwang.fastadapter.databinding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.logging.KL
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

interface VhModel {
    fun vh(): GenericItem
}

abstract class BindingItem<Binding : ViewDataBinding>(open val data: Any?) :
    AbstractItem<BindingItem.ViewHolder>(),
    BindingLayout<Binding> {

    override val type: Int
        get() = layoutRes

    override fun createView(ctx: Context, parent: ViewGroup?): View {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(ctx),
            layoutRes, parent,
            false
        )
        return binding.root
    }

    fun getBinding(holder: ViewHolder): Binding? =
        DataBindingUtil.getBinding<Binding>(holder.itemView)

    final override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        val binding = getBinding(holder) ?: return
        binding.bindView(holder, payloads)
        binding.executePendingBindings()
    }

    abstract fun Binding.bindView(holder: ViewHolder, payloads: MutableList<Any>)

    final override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        val binding = DataBindingUtil.getBinding<Binding>(holder.itemView) ?: return
        binding.unbindView(holder)
        binding.unbind()
    }

    open fun Binding.unbindView(holder: ViewHolder) {}

    final override fun getViewHolder(v: View): ViewHolder = ViewHolder(v, layoutRes)

    override fun failedToRecycle(holder: ViewHolder): Boolean {
        KL.e { "Failed to recycle" }
        return super.failedToRecycle(holder)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BindingItem<*>) return false
        return identifier == other.identifier && data == other.data
    }

    override fun hashCode(): Int = data.hashCode()

    class ViewHolder(itemView: View, internal val layoutRes: Int) :
        RecyclerView.ViewHolder(itemView)
}

interface BindingLayout<Binding : ViewDataBinding> {
    val layoutRes: Int
}

abstract class BindingClickEventHook<Binding : ViewDataBinding, Item : BindingItem<Binding>>(val identifier: BindingLayout<Binding>) :
    ClickEventHook<Item>() {

    private fun RecyclerView.ViewHolder.binding(): Binding? {
        val holder = this as? BindingItem.ViewHolder ?: return null
        if (holder.layoutRes != identifier.layoutRes) {
            return null
        }
        return DataBindingUtil.getBinding(itemView)
    }

    final override fun onBind(viewHolder: RecyclerView.ViewHolder): View? =
        viewHolder.binding()?.onBind(viewHolder) ?: super.onBind(viewHolder)

    open fun Binding.onBind(viewHolder: RecyclerView.ViewHolder): View? = super.onBind(viewHolder)

    final override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? =
        viewHolder.binding()?.onBindMany(viewHolder) ?: super.onBindMany(viewHolder)

    open fun Binding.onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? =
        super.onBindMany(viewHolder)

    final override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item) {
        val binding: Binding = DataBindingUtil.findBinding(v) ?: return
        binding.onClick(v, position, fastAdapter, item)
    }

    abstract fun Binding.onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item)
}