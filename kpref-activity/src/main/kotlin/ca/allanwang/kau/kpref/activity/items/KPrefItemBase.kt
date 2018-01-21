package ca.allanwang.kau.kpref.activity.items

import android.support.annotation.CallSuper
import android.view.View
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KClick
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
            base.setter(value)
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
            override val innerView: View? by lazy { itemView.findViewById<View>(R.id.kau_pref_inner_content) }
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
            background = holder.itemView.context.resolveDrawable(android.R.attr.selectableItemBackground)
            alpha = 1.0f
        }
    }

    final override fun getLayoutRes(): Int = R.layout.kau_pref_core

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
        val setter: (value: T) -> Unit
    }

    /**
     * Default implementation of [BaseContract]
     */
    class BaseBuilder<T>(globalOptions: GlobalOptions,
                         titleId: Int,
                         override val getter: () -> T,
                         override val setter: (value: T) -> Unit
    ) : CoreContract by CoreBuilder(globalOptions, titleId), BaseContract<T> {
        override var enabler: () -> Boolean = { true }
        override var onClick: (KClick<T>.() -> Unit)? = null
        override var onDisabledClick: (KClick<T>.() -> Unit)? = null
    }

}