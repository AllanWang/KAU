package ca.allanwang.kau.mediapicker

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.bindView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.mikepenz.fastadapter.FastAdapter

/**
 * Created by Allan Wang on 2017-07-04.
 */
class MediaItem(val data: MediaModel)
    : KauIItem<MediaItem, MediaItem.ViewHolder>(R.layout.kau_iitem_image, { ViewHolder(it) }) {

    private var failedToLoad = false

    companion object {
        fun bindEvents(fastAdapter: FastAdapter<MediaItem>) {
            fastAdapter.withMultiSelect(true)
                    .withSelectable(true)
                    //adapter selector occurs before the on click event
                    .withOnClickListener { v, _, item, _ ->
                        val image = v as BlurredImageView
                        if (item.isSelected) image.blur()
                        else image.removeBlur()
                        true
                    }
        }
    }

    override fun isSelectable(): Boolean = !failedToLoad

    override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
        super.bindView(holder, payloads)
        Glide.with(holder.itemView)
                .load(data.data)
                .apply(RequestOptions().override(MediaPickerCore.viewSize(holder.itemView.context)))
                .thumbnail(0.5f)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        failedToLoad = true
                        holder.container.imageBase.setImageDrawable(MediaPickerCore.getErrorDrawable(holder.itemView.context))
                        return true;
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        holder.container.imageBase.setImageDrawable(resource)
                        if (isSelected) holder.container.blurInstantly()
                        return true;
                    }
                })
                .into(holder.container.imageBase)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        Glide.with(holder.itemView).clear(holder.container.imageBase)
        holder.container.removeBlurInstantly()
        failedToLoad = false
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val container: BlurredImageView by bindView(R.id.kau_image)
    }
}