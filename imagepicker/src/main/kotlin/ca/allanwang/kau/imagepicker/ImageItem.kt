package ca.allanwang.kau.imagepicker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.gone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-07-04.
 */
class ImageItem(val data: ImageModel)
    : KauIItem<ImageItem, ImageItem.ViewHolder>(R.layout.kau_iitem_image, { ViewHolder(it) }) {

    private var failedToLoad = false
    var withFade = true

    companion object {
        fun bindEvents(fastAdapter: FastAdapter<ImageItem>) {
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
        if (withFade) holder.container.alpha = 0f
        Glide.with(holder.itemView)
                .load(data.data)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        failedToLoad = true;
                        holder.container.setIcon(GoogleMaterial.Icon.gmd_error);
                        if (withFade) holder.container.animate().alpha(1f).start();
                        return true;
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        holder.container.imageBase.setImageDrawable(resource)
                        if (isSelected) holder.container.blurInstantly()
                        if (withFade) holder.container.animate().alpha(1f).start();
                        return true;
                    }
                })
                .into(holder.container.imageBase)
    }

    private fun BlurredImageView.setIcon(icon: IIcon) {
        val sizePx = computeViewSize(context)
        imageBase.setImageDrawable(IconicsDrawable(context, icon)
                .sizePx(sizePx)
                .paddingPx(sizePx / 3)
                .color(Color.WHITE))
        imageBase.setBackgroundColor(ImagePickerActivityBase.accentColor)
        imageForeground.gone()
    }

    private fun computeViewSize(context: Context): Int {
        val screenWidthPx = context.resources.displayMetrics.widthPixels
        return screenWidthPx / ImagePickerActivityBase.computeColumnCount(context)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        if (!failedToLoad) {
            Glide.with(holder.itemView).clear(holder.container.imageBase)
            holder.container.removeBlurInstantly()
        } else {
            holder.container.fullReset()
        }
        failedToLoad = false
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val container: BlurredImageView by bindView(R.id.kau_image)
    }
}