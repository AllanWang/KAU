/*
 * Copyright 2018 Allan Wang
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
package ca.allanwang.kau.kpref.activity.items

import android.view.View
import androidx.annotation.CallSuper
import ca.allanwang.kau.kotlin.lazyUi
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.KPrefItemActions
import ca.allanwang.kau.kpref.activity.R
import ca.allanwang.kau.utils.resolveDrawable

/**
 * Created by Allan Wang on 2017-06-05.
 *
 * Base class for pref setters that include the Shared Preference hooks
 */
abstract class KPrefItemBase<T>(protected val base: BaseContract<T>) : KPrefItemCore(base) {

    open var pref: T
        get() = base.getter()
        set(value) {
            base.setter(this, value)
        }

    private var _enabled: Boolean = true

    val enabled
        get() = _enabled

    init {
        if (base.onClick == null) base.onClick = { defaultOnClick() }
    }

    @CallSuper
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        _enabled = base.enabler()
        with(holder) {
            if (!_enabled) container?.background = null
            container?.alpha = if (_enabled) 1.0f else 0.3f
        }
    }

    final override fun onClick(itemView: View) {
        val kclick = object : KClick<T> {
            override val context = itemView.context
            override val itemView = itemView
            override val innerView: View? by lazyUi { itemView.findViewById<View>(R.id.kau_pref_inner_content) }
            override val item = this@KPrefItemBase
        }
        if (_enabled) {
            val onClick = base.onClick ?: return
            kclick.onClick()
        } else {
            val onClick = base.onDisabledClick ?: return
            kclick.onClick()
        }
    }

    abstract fun KClick<T>.defaultOnClick()

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.container?.apply {
            isEnabled = true
            background =
                holder.itemView.context.resolveDrawable(android.R.attr.selectableItemBackground)
            alpha = 1.0f
        }
    }

    final override val layoutRes: Int
        get() = R.layout.kau_pref_core

    /**
     * Extension of the core contract
     * Since everything that extends the base is an actual preference, there must be a getter and setter
     * The rest are optional and will have their defaults
     */
    interface BaseContract<T> : CoreContract {
        var enabler: () -> Boolean
        var onClick: (KClick<T>.() -> Unit)?
        var onDisabledClick: (KClick<T>.() -> Unit)?
        val getter: () -> T
        val setter: KPrefItemActions.(value: T) -> Unit
    }

    /**
     * Default implementation of [BaseContract]
     */
    class BaseBuilder<T>(
        globalOptions: GlobalOptions,
        titleId: Int,
        override val getter: () -> T,
        override val setter: KPrefItemActions.(value: T) -> Unit
    ) : CoreContract by CoreBuilder(globalOptions, titleId), BaseContract<T> {
        override var enabler: () -> Boolean = { true }
        override var onClick: (KClick<T>.() -> Unit)? = null
        override var onDisabledClick: (KClick<T>.() -> Unit)? = null
    }
}
