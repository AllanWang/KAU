package ca.allanwang.kau.kpref.items

import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.GlobalOptions
import ca.allanwang.kau.kpref.KPrefAdapterBuilder

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Sub item preference
 * When clicked, will navigate to a new set of preferences and add the old list to a stack
 *
 */
class KPrefSubItems(val builder: KPrefSubItemsContract) : KPrefItemCore(builder) {

    override fun onClick(itemView: View, innerContent: View?): Boolean {
        builder.globalOptions.showNextPrefs(builder.titleRes, builder.itemBuilder)
        return true
    }

    override fun getLayoutRes(): Int = R.layout.kau_preference

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {}
    /**
     * Extension of the base contract with an optional text getter
     */
    interface KPrefSubItemsContract : CoreContract {
        val itemBuilder: KPrefAdapterBuilder.() -> Unit
    }

    /**
     * Default implementation of [KPrefTextContract]
     */
    class KPrefSubItemsBuilder(
            globalOptions: GlobalOptions,
            titleRes: Int,
            override val itemBuilder: KPrefAdapterBuilder.() -> Unit
    ) : KPrefSubItemsContract, CoreContract by CoreBuilder(globalOptions, titleRes)

    override fun getType(): Int = R.id.kau_item_pref_sub_item

}