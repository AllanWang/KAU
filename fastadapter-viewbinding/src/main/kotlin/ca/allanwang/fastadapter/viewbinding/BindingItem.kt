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
package ca.allanwang.fastadapter.viewbinding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ca.allanwang.kau.fastadapter.viewbinding.R
import ca.allanwang.kau.logging.KL
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

interface VhModel {
    fun vh(): GenericItem
}

/**
 * Layout container. Should be implemented in a [BindingItem] companion.
 */
interface BindingLayout<Binding : ViewBinding> {
    val layoutRes: Int
}

abstract class BindingItem<Binding : ViewBinding>(open val data: Any?) :
    AbstractItem<BindingItem.ViewHolder>(),
    BindingLayout<Binding> {

    override val type: Int
        get() = layoutRes

    abstract fun createBinding(layoutInflater: LayoutInflater, parent: ViewGroup?): Binding

    override fun createView(ctx: Context, parent: ViewGroup?): View {
        val binding = createBinding(LayoutInflater.from(ctx), parent)
        setBinding(binding.root, binding)
        return binding.root
    }

    final override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        val binding = holder.getBinding<Binding>()
        binding.bindView(holder, payloads)
    }

    abstract fun Binding.bindView(holder: ViewHolder, payloads: MutableList<Any>)

    final override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        val binding = holder.getBinding<Binding>()
        binding.unbindView(holder)
    }

    open fun Binding.unbindView(holder: ViewHolder) {}

    final override fun getViewHolder(v: View): ViewHolder =
        ViewHolder(v, layoutRes)

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
        RecyclerView.ViewHolder(itemView) {

        /**
         * Retrieves a binding.
         *
         * It is assumed that the binding is set prior to this call,
         * and that its type matches the supplied generic.
         */
        fun <T> getBinding(): T = getBinding(itemView)
    }

    companion object {
        fun setBinding(view: View, binding: Any) {
            view.setTag(R.id.kau_view_binding_model, binding)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> getBinding(view: View): T = view.getTag(R.id.kau_view_binding_model) as T
    }
}

abstract class BindingClickEventHook<Binding : ViewBinding, Item : BindingItem<Binding>>(val identifier: BindingLayout<Binding>) :
    ClickEventHook<Item>() {

    private fun RecyclerView.ViewHolder.binding(): Binding? {
        val holder = this as? BindingItem.ViewHolder ?: return null
        if (holder.layoutRes != identifier.layoutRes) {
            return null
        }
        return getBinding()
    }

    final override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        val binding = viewHolder.binding() ?: return super.onBind(viewHolder)
        val view = binding.onBind(viewHolder) ?: return super.onBind(viewHolder)
        BindingItem.setBinding(view, binding)
        return view
    }

    open fun Binding.onBind(viewHolder: RecyclerView.ViewHolder): View? = super.onBind(viewHolder)

    final override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
        val binding = viewHolder.binding() ?: return super.onBindMany(viewHolder)
        val views = binding.onBindMany(viewHolder) ?: return super.onBindMany(viewHolder)
        views.forEach { BindingItem.setBinding(it, binding) }
        return views
    }

    open fun Binding.onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? =
        super.onBindMany(viewHolder)

    final override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item) {
        BindingItem.getBinding<Binding>(v).onClick(v, position, fastAdapter, item)
    }

    abstract fun Binding.onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item)
}