package ca.allanwang.kau.kpref.activity.items

import android.widget.TextView
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.R
import ca.allanwang.kau.utils.toast

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Text preference
 * Holds a textview to display data on the right
 * This is still a generic preference
 *
 */
open class KPrefText<T>(open val builder: KPrefTextContract<T>) : KPrefItemBase<T>(builder) {

    /**
     * Automatically reload on set
     */
    override var pref: T
        get() = base.getter()
        set(value) {
            base.setter(value)
            builder.reloadSelf()
        }

    override fun KClick<T>.defaultOnClick() {
        context.toast("No click function set")
    }

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        super.onPostBindView(viewHolder, textColor, accentColor)
        val textview = viewHolder.bindInnerView<TextView>(R.layout.kau_pref_text)
        if (textColor != null) textview.setTextColor(textColor)
        textview.text = builder.textGetter(pref)
    }

    /**
     * Extension of the base contract with an optional text getter
     */
    interface KPrefTextContract<T> : BaseContract<T> {
        var textGetter: (T) -> String?
    }

    /**
     * Default implementation of [KPrefTextContract]
     */
    class KPrefTextBuilder<T>(
            globalOptions: GlobalOptions,
            titleId: Int,
            getter: () -> T,
            setter: (value: T) -> Unit
    ) : KPrefTextContract<T>, BaseContract<T> by BaseBuilder<T>(globalOptions, titleId, getter, setter) {
        override var textGetter: (T) -> String? = { it?.toString() }
    }

    override fun getType(): Int = R.id.kau_item_pref_text

}