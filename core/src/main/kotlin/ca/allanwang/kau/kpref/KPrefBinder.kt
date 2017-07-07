package ca.allanwang.kau.kpref

import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.items.*
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Allan Wang on 2017-06-08.
 *
 * Houses all the components that can be called externally to setup the kpref mainAdapter
 */

/**
 * Base extension that will register the layout manager and mainAdapter with the given items
 * Returns FastAdapter
 */
fun RecyclerView.setKPrefAdapter(globalOptions: GlobalOptions, builder: KPrefAdapterBuilder.() -> Unit): FastItemAdapter<KPrefItemCore> {
    layoutManager = LinearLayoutManager(context)
    val adapter = FastItemAdapter<KPrefItemCore>()
    adapter.withOnClickListener { v, _, item, _ -> item.onClick(v, v.findViewById(R.id.kau_pref_inner_content)) }
    this.adapter = adapter
    doAsync {
        val items = KPrefAdapterBuilder(globalOptions)
        builder.invoke(items)
        uiThread {
            adapter.add(items.list)
        }
    }
    return adapter
}

@DslMarker
annotation class KPrefMarker

/**
 * Contains attributes shared amongst all kpref items
 */
@KPrefMarker
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
    fun showNextPrefs(@StringRes toolbarTitleRes: Int, builder: KPrefAdapterBuilder.() -> Unit)
    fun showPrevPrefs()
    fun reloadByTitle(@StringRes vararg title: Int)
}


class GlobalOptions(core: CoreAttributeContract, activity: KPrefActivityContract
) : CoreAttributeContract by core, KPrefActivityContract by activity


/**
 * Builder for kpref items
 * Contains DSLs for every possible item
 * The arguments are all the mandatory values plus an optional builder housing all the possible configurations
 * The mandatory values are final so they cannot be edited in the builder
 */
@KPrefMarker
class KPrefAdapterBuilder(internal val globalOptions: GlobalOptions) {

    @KPrefMarker
    fun header(@StringRes title: Int)
            = list.add(KPrefHeader(KPrefItemCore.CoreBuilder(globalOptions, title)))

    @KPrefMarker
    fun checkbox(@StringRes title: Int,
                 getter: (() -> Boolean),
                 setter: ((value: Boolean) -> Unit),
                 builder: KPrefItemBase.BaseContract<Boolean>.() -> Unit = {})
            = list.add(KPrefCheckbox(KPrefItemBase.BaseBuilder(globalOptions, title, getter, setter)
            .apply { builder() }))

    @KPrefMarker
    fun colorPicker(@StringRes title: Int,
                    getter: (() -> Int),
                    setter: ((value: Int) -> Unit),
                    builder: KPrefColorPicker.KPrefColorContract.() -> Unit = {})
            = list.add(KPrefColorPicker(KPrefColorPicker.KPrefColorBuilder(globalOptions, title, getter, setter)
            .apply { builder() }))

    @KPrefMarker
    fun <T> text(@StringRes title: Int,
                 getter: (() -> T),
                 setter: ((value: T) -> Unit),
                 builder: KPrefText.KPrefTextContract<T>.() -> Unit = {})
            = list.add(KPrefText<T>(KPrefText.KPrefTextBuilder<T>(globalOptions, title, getter, setter)
            .apply { builder() }))

    @KPrefMarker
    fun subItems(@StringRes title: Int,
                 itemBuilder: KPrefAdapterBuilder.() -> Unit,
                 builder: KPrefSubItems.KPrefSubItemsContract.() -> Unit)
            = list.add(KPrefSubItems(KPrefSubItems.KPrefSubItemsBuilder(globalOptions, title, itemBuilder)
            .apply { builder() }))

    @KPrefMarker
    fun plainText(@StringRes title: Int,
                  builder: KPrefItemBase.BaseContract<Unit>.() -> Unit = {})
            = list.add(KPrefPlainText(KPrefPlainText.KPrefPlainTextBuilder(globalOptions, title)
            .apply { builder() }))

    @KPrefMarker
    fun seekbar(@StringRes title: Int,
                getter: (() -> Int),
                setter: ((value: Int) -> Unit),
                builder: KPrefSeekbar.KPrefSeekbarContract.() -> Unit = {})
            = list.add(KPrefSeekbar(KPrefSeekbar.KPrefSeekbarBuilder(globalOptions, title, getter, setter)
            .apply { builder() }))

    @KPrefMarker
    val list: MutableList<KPrefItemCore> = mutableListOf()
}