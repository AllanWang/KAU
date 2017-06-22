package ca.allanwang.kau.kpref

import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.items.*
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-06-08.
 *
 * Houses all the components that can be called externally to setup the kpref adapter
 */

/**
 * Base extension that will register the layout manager and adapter with the given items
 * Returns FastAdapter
 */
fun RecyclerView.setKPrefAdapter(globalOptions: GlobalOptions, builder: KPrefAdapterBuilder.() -> Unit): FastItemAdapter<KPrefItemCore> {
    layoutManager = LinearLayoutManager(context)
    val adapter = FastItemAdapter<KPrefItemCore>()
    adapter.withOnClickListener { v, _, item, _ -> item.onClick(v, v.findViewById(R.id.kau_pref_inner_content)) }
    val items = KPrefAdapterBuilder(globalOptions)
    builder.invoke(items)
    adapter.add(items.list)
    this.adapter = adapter
    return adapter
}

/**
 * Contains attributes shared amongst all kpref items
 */
interface CoreAttributeContract {
    var textColor: (() -> Int)?
    var accentColor: (() -> Int)?
}

/**
 * Implementation of [CoreAttributeContract]
 */
class CoreAttributeBuilder : CoreAttributeContract {
    override var textColor: (() -> Int)? = null
    override var accentColor: (() -> Int)? = textColor
}

interface KPrefActivityContract {
    fun showNextPrefs(@StringRes toolbarTitleRes:Int, builder: KPrefAdapterBuilder.() -> Unit)
    fun showPrevPrefs()
}


class GlobalOptions(core: CoreAttributeContract, activity: KPrefActivityContract
) : CoreAttributeContract by core, KPrefActivityContract by activity


/**
 * Builder for kpref items
 * Contains DSLs for every possible item
 * The arguments are all the mandatory values plus an optional builder housing all the possible configurations
 * The mandatory values are final so they cannot be edited in the builder
 */
class KPrefAdapterBuilder(internal val globalOptions: GlobalOptions) {

    fun header(@StringRes title: Int)
            = list.add(KPrefHeader(KPrefItemCore.CoreBuilder(globalOptions, title)))

    fun checkbox(@StringRes title: Int,
                 getter: (() -> Boolean),
                 setter: ((value: Boolean) -> Unit),
                 builder: KPrefItemBase.BaseContract<Boolean>.() -> Unit = {})
            = list.add(KPrefCheckbox(KPrefItemBase.BaseBuilder(globalOptions, title, getter, setter)
            .apply { builder() }))


    fun colorPicker(@StringRes title: Int,
                    getter: (() -> Int),
                    setter: ((value: Int) -> Unit),
                    builder: KPrefColorPicker.KPrefColorContract.() -> Unit = {})
            = list.add(KPrefColorPicker(KPrefColorPicker.KPrefColorBuilder(globalOptions, title, getter, setter)
            .apply { builder() }))

    fun <T> text(@StringRes title: Int,
                 getter: (() -> T),
                 setter: ((value: T) -> Unit),
                 builder: KPrefText.KPrefTextContract<T>.() -> Unit = {})
            = list.add(KPrefText<T>(KPrefText.KPrefTextBuilder<T>(globalOptions, title, getter, setter)
            .apply { builder() }))

    fun subItems(@StringRes title: Int,
                 itemBuilder: KPrefAdapterBuilder.() -> Unit,
                 builder: KPrefSubItems.KPrefSubItemsContract.() -> Unit)
            = list.add(KPrefSubItems(KPrefSubItems.KPrefSubItemsBuilder(globalOptions, title, itemBuilder)
            .apply { builder() }))

    internal val list: MutableList<KPrefItemCore> = mutableListOf()
}