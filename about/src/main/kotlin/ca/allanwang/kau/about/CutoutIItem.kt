package ca.allanwang.kau.about

import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.ui.views.CutoutView

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Just a cutout item with some defaults in [R.layout.kau_iitem_cutout]
 */
class CutoutIItem(val config: CutoutView.() -> Unit = {}) : KauIItem<CutoutIItem, CutoutIItem.ViewHolder>(
        R.layout.kau_iitem_cutout, ::ViewHolder, R.id.kau_item_cutout
), ThemableIItem by ThemableIItemDelegate() {

    override fun isSelectable(): Boolean = false

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        with(holder) {
            if (accentColor != null && themeEnabled) cutout.foregroundColor = accentColor!!
            cutout.config()
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            cutout.drawable = null
            cutout.text = "Text" //back to default
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cutout: CutoutView = v.findViewById(R.id.kau_cutout)
    }

}