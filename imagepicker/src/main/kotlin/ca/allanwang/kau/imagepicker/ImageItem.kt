package ca.allanwang.kau.imagepicker

import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.bindView
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import timber.log.Timber

/**
 * Created by Allan Wang on 2017-07-04.
 */
class ImageItem(val data: ImageModel)
    : KauIItem<ImageItem, ImageItem.ViewHolder>(R.layout.kau_iitem_image, { ViewHolder(it) }) {

    fun bindEvents(fastAdapter: FastAdapter<ImageItem>) {
        fastAdapter.withPositionBasedStateManagement(false)
        fastAdapter.withMultiSelect(true)
        fastAdapter.withSelectable(true)
        fastAdapter.withOnClickListener { v, adapter, item, position ->
            Timber.d("SELECT %b", item.isSelected())
            val image = v as BlurredImageView
            image.toggleBlur()
            true
        }
    }

    override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
        super.bindView(holder, payloads)
        Glide.with(holder.itemView)
                .load(data.data)
                .into(holder.container.imageBase)
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)
        Glide.with(holder!!.itemView).clear(holder.container.imageBase)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val container: BlurredImageView by bindView(R.id.kau_image)
    }
}