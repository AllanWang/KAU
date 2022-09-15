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

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.iitems.KauIItem
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.select.getSelectExtension

/** Created by Allan Wang on 2017-07-04. */
class MediaItem(val data: MediaModel) :
  KauIItem<MediaItem.ViewHolder>(R.layout.kau_iitem_image, { ViewHolder(it) }),
  GlideContract by GlideDelegate() {

  private var failedToLoad = false

  companion object {
    fun bindEvents(fastAdapter: FastAdapter<MediaItem>) {
      fastAdapter.getSelectExtension().apply {
        isSelectable = true
        multiSelect = true
      }
      fastAdapter.onClickListener = { v, _, item, _ ->
        val image = v as BlurredImageView
        if (item.isSelected) image.blur() else image.removeBlur()
        true
      }
    }
  }

  override var isSelectable: Boolean
    get() = !failedToLoad
    set(_) {}

  override fun bindView(holder: ViewHolder, payloads: List<Any>) {
    super.bindView(holder, payloads)
    glide(holder.itemView)
      .load(data.data)
      .applyMediaOptions(holder.itemView.context)
      .listener(
        object : RequestListener<Drawable> {
          override fun onLoadFailed(
            e: GlideException?,
            model: Any,
            target: Target<Drawable>,
            isFirstResource: Boolean
          ): Boolean {
            failedToLoad = true
            holder.container.imageBase.setImageDrawable(
              MediaPickerCore.getErrorDrawable(holder.itemView.context)
            )
            return true
          }

          override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>,
            dataSource: DataSource,
            isFirstResource: Boolean
          ): Boolean {
            holder.container.imageBase.setImageDrawable(resource)
            if (isSelected) holder.container.blurInstantly()
            return true
          }
        }
      )
      .into(holder.container.imageBase)
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    glide(holder.itemView).clear(holder.container.imageBase)
    holder.container.removeBlurInstantly()
    failedToLoad = false
  }

  class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val container: BlurredImageView = v.findViewById(R.id.kau_image)
  }
}
