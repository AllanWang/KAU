package ca.allanwang.kau.kpref

import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.dialogs.color.Builder
import ca.allanwang.kau.kpref.items.KPrefCheckbox
import ca.allanwang.kau.kpref.items.KPrefColorPicker
import ca.allanwang.kau.kpref.items.KPrefHeader
import ca.allanwang.kau.kpref.items.KPrefItemCore
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-08.
 */
fun RecyclerView.setKPrefAdapter(builder: KPrefAdapterBuilder.() -> Unit): FastItemAdapter<KPrefItemCore> {
    layoutManager = LinearLayoutManager(context)
    val adapter = FastItemAdapter<KPrefItemCore>()
    adapter.withOnClickListener { v, _, item, _ -> item.onClick(v) }
    val items = KPrefAdapterBuilder()
    builder.invoke(items)
    adapter.add(items.list)
    this.adapter = adapter
    return adapter
}

class KPrefAdapterBuilder {

    var textColor: Int? = null
    var accentColor: Int? = null

    fun header(@StringRes title: Int) = list.add(KPrefHeader(this, title))

    fun checkbox(@StringRes title: Int,
                 @StringRes description: Int = -1,
                 iicon: IIcon? = null,
                 enabled: Boolean = true,
                 getter: () -> Boolean,
                 setter: (value: Boolean) -> Unit) = list.add(KPrefCheckbox(this, title, description, iicon, enabled, getter, setter))

    fun colorPicker(@StringRes title: Int,
                    @StringRes description: Int = -1,
                    iicon: IIcon? = null,
                    enabled: Boolean = true,
                    getter: () -> Int,
                    setter: (value: Int) -> Unit,
                    configs: Builder.() -> Unit = {}) = list.add(KPrefColorPicker(this, title, description, iicon, enabled, getter, setter, configs))

    internal val list: MutableList<KPrefItemCore> = mutableListOf()

}