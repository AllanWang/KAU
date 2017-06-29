package ca.allanwang.kau.iitems

import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.views.CutoutView
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Just a cutout item with some defaults in [R.layout.kau_iitem_cutout]
 */
class CutoutIItem(val config: CutoutView.() -> Unit = {}
) : AbstractItem<CutoutIItem, CutoutIItem.ViewHolder>(), ThemableIItem by ThemableIItemDelegate() {

    override fun getType(): Int = R.id.kau_item_cutout

    override fun getLayoutRes(): Int = R.layout.kau_iitem_cutout

    override fun isSelectable(): Boolean = false

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
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

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cutout: CutoutView by bindView(R.id.kau_cutout)
    }

}