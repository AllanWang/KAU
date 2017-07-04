package ca.allanwang.kau.kpref.items

import android.view.View
import android.widget.TextView
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.GlobalOptions
import ca.allanwang.kau.utils.toast

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Text preference
 * Holds a textview to display data on the right
 * This is still a generic preference
 *
 */
class KPrefText<T>(val builder: KPrefTextContract<T>) : KPrefItemBase<T>(builder) {

    /**
     * Automatically reload on set
     */
    override var pref: T
        get() = base.getter.invoke()
        set(value) {
            base.setter.invoke(value)
            builder.reloadSelf()
        }

    override fun defaultOnClick(itemView: View, innerContent: View?): Boolean {
        itemView.context.toast("No click function set")
        return true
    }

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        super.onPostBindView(viewHolder, textColor, accentColor)
        val textview = viewHolder.bindInnerView<TextView>(R.layout.kau_preference_text)
        if (textColor != null) textview.setTextColor(textColor)
        textview.text = builder.textGetter.invoke(pref)
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
            titleRes: Int,
            getter: () -> T,
            setter: (value: T) -> Unit
    ) : KPrefTextContract<T>, BaseContract<T> by BaseBuilder<T>(globalOptions, titleRes, getter, setter) {
        override var textGetter: (T) -> String? = { it?.toString() }
    }

    override fun getType(): Int = R.id.kau_item_pref_text

}