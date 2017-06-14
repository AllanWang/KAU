package ca.allanwang.kau.kpref

import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.R
import ca.allanwang.kau.dialogs.color.Builder
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

    fun header(@StringRes title: Int) = list.add(KPrefHeader(this, title))

    fun checkbox(@StringRes title: Int,
                 coreBuilder: KPrefItemCore.Builder.() -> Unit = {},
                 itemBuilder: KPrefItemBase.Builder<Boolean>.() -> Unit = {}
    ) = list.add(KPrefCheckbox(this, title, coreBuilder, itemBuilder))

    fun colorPicker(@StringRes title: Int,
                    coreBuilder: KPrefItemCore.Builder.() -> Unit = {},
                    itemBuilder: KPrefItemBase.Builder<Int>.() -> Unit = {},
                    colorBuilder: Builder.() -> Unit = {},
                    showPreview: Boolean = true
    ) = list.add(KPrefColorPicker(this, title, coreBuilder, itemBuilder, colorBuilder, showPreview))

    fun <T> text(@StringRes title: Int,
                 coreBuilder: KPrefItemCore.Builder.() -> Unit = {},
                 itemBuilder: KPrefItemBase.Builder<T>.() -> Unit = {},
                 textGetter: (T) -> String? = { it?.toString() }
    ) = list.add(KPrefText<T>(this, title, coreBuilder, itemBuilder, textGetter))

    internal val list: MutableList<KPrefItemCore> = mutableListOf()

}