package ca.allanwang.kau.imagepicker

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.bindView

/**
 * Created by Allan Wang on 2017-07-04.
 */
class ImageItem(data:String)
    : KauIItem<ImageItem, ImageItem.ViewHolder>(R.layout.kau_iitem_card, { ViewHolder(it) }) {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val image: ImageView by bindView(R.id.kau_image)
    }
}