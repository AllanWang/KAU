/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.mediapicker

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.ui.views.MeasuredImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.select.getSelectExtension

/**
 * Created by Allan Wang on 2017-07-04.
 */
class MediaItemBasic(val data: MediaModel) :
    KauIItem<MediaItemBasic.ViewHolder>(R.layout.kau_iitem_image_basic, { ViewHolder(it) }),
    GlideContract by GlideDelegate() {

    companion object {
        @SuppressLint("NewApi")
        fun bindEvents(activity: Activity, fastAdapter: FastAdapter<MediaItemBasic>) {
            fastAdapter.getSelectExtension().isSelectable = true
            //add image data and return right away
            fastAdapter.onClickListener = { _, _, item, _ ->
                activity.finish(arrayListOf(item.data))
                true
            }
        }
    }

    override var isSelectable: Boolean
        get() = false
        set(_) {}

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        glide(holder.itemView)
            .load(data.data)
            .applyMediaOptions(holder.itemView.context)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.image.setImageDrawable(MediaPickerCore.getErrorDrawable(holder.itemView.context))
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
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
        val image: MeasuredImageView = v.findViewById(R.id.kau_image)
    }
}
