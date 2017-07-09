package ca.allanwang.kau.kpref.activity.items

import android.support.annotation.CallSuper
import android.view.View
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.R
import ca.allanwang.kau.utils.resolveDrawable

/**
 * Created by Allan Wang on 2017-06-05.
 *
 * Base class for pref setters that include the Shared Preference hooks
 */
abstract class KPrefItemBase<T>(val base: BaseContract<T>) : KPrefItemCore(base) {

    open var pref: T
        get() = base.getter.invoke()
        set(value) {
            base.setter.invoke(value)
        }

    var enabled: Boolean = true

    init {
        if (base.onClick == null) base.onClick = {
            itemView, innerContent, _ ->
            defaultOnClick(itemView, innerContent)
        }
    }

    abstract fun defaultOnClick(itemView: View, innerContent: View?): Boolean

    @CallSuper
    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        enabled = base.enabler.invoke()
        with(viewHolder) {
            if (!enabled) container?.background = null
            container?.alpha = if (enabled) 1.0f else 0.3f
        }
    }

    override final fun onClick(itemView: View, innerContent: View?): Boolean {
        return if (enabled) base.onClick?.invoke(itemView, innerContent, this) ?: false
        else base.onDisabledClick?.invoke(itemView, innerContent, this) ?: false
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            container?.isEnabled = true
            container?.background = itemView.context.resolveDrawable(android.R.attr.selectableItemBackground)
            container?.alpha = 1.0f
        }
    }

    override final fun getLayoutRes(): Int = R.layout.kau_pref_core

    /**
     * Extension of the core contract
     * Since everything that extends the base is an actual preference, there must be a getter and setter
     * The rest are optional and will have their defaults
     */
    interface BaseContract<T> : CoreContract {
        var enabler: () -> Boolean
        var onClick: ((itemView: View, innerContent: View?, item: KPrefItemBase<T>) -> Boolean)?
        var onDisabledClick: ((itemView: View, innerContent: View?, item: KPrefItemBase<T>) -> Boolean)?
        val getter: () -> T
        val setter: (value: T) -> Unit
    }

    /**
     * Default implementation of [BaseContract]
     */
    class BaseBuilder<T>(globalOptions: GlobalOptions,
                         titleRes: Int,
                         override val getter: () -> T,
                         override val setter: (value: T) -> Unit
    ) : CoreContract by CoreBuilder(globalOptions, titleRes), BaseContract<T> {
        override var enabler: () -> Boolean = { true }
        override var onClick: ((itemView: View, innerContent: View?, item: KPrefItemBase<T>) -> Boolean)? = null
        override var onDisabledClick: ((itemView: View, innerContent: View?, item: KPrefItemBase<T>) -> Boolean)? = null
    }

}