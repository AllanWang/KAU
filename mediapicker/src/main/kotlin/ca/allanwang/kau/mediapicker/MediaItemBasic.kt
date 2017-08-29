package ca.allanwang.kau.mediapicker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.ui.views.MeasuredImageView
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.buildIsLollipopAndUp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mikepenz.fastadapter.FastAdapter

/**
 * Created by Allan Wang on 2017-07-04.
 */
class MediaItemBasic(val data: MediaModel)
    : KauIItem<MediaItem, MediaItemBasic.ViewHolder>(R.layout.kau_iitem_image_basic, { ViewHolder(it) }), GlideContract by GlideDelegate() {

    companion object {
        @SuppressLint("NewApi")
        fun bindEvents(activity: Activity, fastAdapter: FastAdapter<MediaItemBasic>) {
            fastAdapter.withSelectable(false)
                    //add image data and return right away
                    .withOnClickListener { _, _, item, _ ->
                        activity.finish(arrayListOf(item.data))
                        true
                    }
        }
    }

    override fun isSelectable(): Boolean = false

    override fun bindView(holder: ViewHolder, payloads: List<Any>?) {
        super.bindView(holder, payloads)
        glide(holder.itemView)
                .load(data.data)
                .applyMediaOptions(holder.itemView.context)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        holder.image.setImageDrawable(MediaPickerCore.getErrorDrawable(holder.itemView.context))
                        return true;
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .into(holder.image)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        glide(holder.itemView).clear(holder.image)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val image: MeasuredImageView by bindView(R.id.kau_image)
    }
}