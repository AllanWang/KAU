package ca.allanwang.kau.kpref.items

import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-05.
 *
 * Base class for pref setters that include the Shared Preference hooks
 */

abstract class KPrefItemBase<T>(builder: KPrefAdapterBuilder,
                                @StringRes title: Int,
                                @StringRes description: Int = -1,
                                iicon: IIcon? = null,
                                val enabler: () -> Boolean = { true },
                                private val getter: () -> T,
                                private val setter: (value: T) -> Unit) : KPrefItemCore(builder, title, description, iicon) {

    var pref: T
        get() = getter.invoke()
        set(value) {
            setter.invoke(value)
        }

    @CallSuper
    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        val enabled = enabler.invoke()
        with(viewHolder) {
            container?.isEnabled = enabled
            container?.alpha = if (enabled) 1.0f else 0.3f
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            container?.isEnabled = true
            container?.alpha = 1.0f
        }
    }

    override final fun getLayoutRes(): Int = R.layout.kau_preference
}