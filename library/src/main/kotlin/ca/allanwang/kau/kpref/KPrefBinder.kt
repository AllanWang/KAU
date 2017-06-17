package ca.allanwang.kau.kpref

import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.items.*
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-06-08.
 */
fun RecyclerView.setKPrefAdapter(builder: KPrefAdapterBuilder.() -> Unit): FastItemAdapter<KPrefItemCore> {
    layoutManager = LinearLayoutManager(context)
    val adapter = FastItemAdapter<KPrefItemCore>()
    adapter.withOnClickListener { v, _, item, _ -> item.onClick(v, v.findViewById(R.id.kau_pref_inner_content)) }
    val items = KPrefAdapterBuilder()
    builder.invoke(items)
    adapter.add(items.list)
    this.adapter = adapter
    return adapter
}

class KPrefAdapterBuilder {

    var textColor: (() -> Int)? = null
    var accentColor: (() -> Int)? = null

    fun header(@StringRes title: Int)
            = list.add(KPrefHeader(this, KPrefItemCore.CoreBuilder()
            .apply {
                titleRes = title
            }))

    fun checkbox(@StringRes title: Int,
                 getter: (() -> Boolean),
                 setter: ((value: Boolean) -> Unit),
                 builder: KPrefItemBase.BaseContract<Boolean>.() -> Unit = {})
            = list.add(KPrefCheckbox(this, KPrefItemBase.BaseBuilder<Boolean>()
            .apply {
                this.titleRes = title
                this.getter = getter
                this.setter = setter
                builder()
            }))


    fun colorPicker(@StringRes title: Int,
                    getter: (() -> Int),
                    setter: ((value: Int) -> Unit),
                    builder: KPrefColorPicker.KPrefColorContract.() -> Unit = {})
            = list.add(KPrefColorPicker(this, KPrefColorPicker.KPrefColorBuilder()
            .apply {
                this.titleRes = title
                this.getter = getter
                this.setter = setter
                builder()
            }))

    fun <T> text(@StringRes title: Int,
                 getter: (() -> T),
                 setter: ((value: T) -> Unit),
                 builder: KPrefText.KPrefTextContract<T>.() -> Unit = {})
            = list.add(KPrefText<T>(this, title, KPrefText.KPrefTextBuilder<T>()
            .apply {
                this.titleRes = title
                this.getter = getter
                this.setter = setter
                builder()
            }))

    internal val list: MutableList<KPrefItemCore> = mutableListOf()

}