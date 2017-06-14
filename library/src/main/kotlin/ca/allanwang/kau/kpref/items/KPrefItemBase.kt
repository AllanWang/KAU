package ca.allanwang.kau.kpref.items

import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.KPrefException
import ca.allanwang.kau.utils.resolveDrawable
import ca.allanwang.kau.utils.string

/**
 * Created by Allan Wang on 2017-06-05.
 *
 * Base class for pref setters that include the Shared Preference hooks
 */

abstract class KPrefItemBase<T>(builder: KPrefAdapterBuilder,
                                @StringRes title: Int,
                                coreBuilder: KPrefItemCore.Builder.() -> Unit = {},
                                itemBuilder: Builder<T>.() -> Unit = {}) : KPrefItemCore(builder, title, coreBuilder) {

    var pref: T
        get() = itemBase.getter!!.invoke()
        set(value) {
            itemBase.setter!!.invoke(value)
        }

    var enabled: Boolean = true
    val itemBase: Builder<T>

    init {
        itemBase = Builder<T>()
        itemBase.itemBuilder()
        if (itemBase.onClick == null) itemBase.onClick = {
            itemView, innerContent, _ ->
            defaultOnClick(itemView, innerContent)
        }
    }

    abstract fun defaultOnClick(itemView: View, innerContent: View?): Boolean

    @CallSuper
    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        val c = viewHolder.itemView.context
        if (itemBase.getter == null) throw KPrefException("getter not set for ${c.string(title)}")
        if (itemBase.setter == null) throw KPrefException("setter not set for ${c.string(title)}")
        enabled = itemBase.enabler.invoke()
        with(viewHolder) {
            if (!enabled) container?.background = null
            container?.alpha = if (enabled) 1.0f else 0.3f
        }
    }

    override final fun onClick(itemView: View, innerContent: View?): Boolean {
        return if (enabled) itemBase.onClick?.invoke(itemView, innerContent, this) ?: false
        else itemBase.onDisabledClick?.invoke(itemView, innerContent, this) ?: false
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            container?.isEnabled = true
            container?.background = itemView.context.resolveDrawable(android.R.attr.selectableItemBackground)
            container?.alpha = 1.0f
        }
    }

    override final fun getLayoutRes(): Int = R.layout.kau_preference

    open class Builder<T> {
        var enabler: () -> Boolean = { true }
        var onClick: ((itemView: View, innerContent: View?, item: KPrefItemBase<T>) -> Boolean)? = null
        var onDisabledClick: ((itemView: View, innerContent: View?, item: KPrefItemBase<T>) -> Boolean)? = null
        var getter: (() -> T)? = null
        var setter: ((value: T) -> Unit)? = null
    }
}