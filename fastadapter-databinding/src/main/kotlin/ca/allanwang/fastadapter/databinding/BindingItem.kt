package ca.allanwang.fastadapter.databinding


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.gitdroid.logger.L
import ca.allanwang.gitdroid.views.BR
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

typealias GenericBindingItem = BindingItem<*>

abstract class BindingItem<Binding : ViewDataBinding>(open val data: Any?) : AbstractItem<BindingItem.ViewHolder>(),
    BindingLayout<Binding> {

    override val type: Int
        get() = layoutRes

    override fun createView(ctx: Context, parent: ViewGroup?): View {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(ctx),
            layoutRes, parent,
            false,
            null
        )
        return binding.root
    }

    final override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        val binding = DataBindingUtil.getBinding<Binding>(holder.itemView) ?: return
        binding.bindView(holder, payloads)
        binding.executePendingBindings()
    }

    open fun Binding.bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        setVariable(BR.model, data)
    }

    final override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        val binding = DataBindingUtil.getBinding<Binding>(holder.itemView) ?: return
        binding.unbindView(holder)
        binding.unbind()
    }

    open fun Binding.unbindView(holder: ViewHolder) {}

    final override fun getViewHolder(v: View): ViewHolder = ViewHolder(v, layoutRes)

    override fun failedToRecycle(holder: ViewHolder): Boolean {
        L.e { "Failed to recycle" }
        return super.failedToRecycle(holder)
    }

    companion object {
        @JvmStatic
        protected fun unbindGlide(vararg imageView: ImageView) {
            if (imageView.isEmpty()) {
                return
            }
            val manager = Glide.with(imageView.first().context)
            imageView.forEach { manager.clear(it) }
        }

        @JvmStatic
        protected fun unbind(vararg imageView: ImageView) {
            imageView.forEach { it.setImageDrawable(null) }
        }

        @JvmStatic
        protected fun unbind(vararg textView: TextView) {
            textView.forEach { it.text = null }
        }
    }

    class ViewHolder(itemView: View, internal val layoutRes: Int) : RecyclerView.ViewHolder(itemView)

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

    open fun Binding.onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? = super.onBindMany(viewHolder)

    final override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item) {
        val binding: Binding = DataBindingUtil.findBinding(v) ?: return
        binding.onClick(v, position, fastAdapter, item)
    }

    abstract fun Binding.onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item)
}