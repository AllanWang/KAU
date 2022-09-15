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
package ca.allanwang.kau.searchview

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.adjustAlpha
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.setRippleBackground
import ca.allanwang.kau.utils.visible
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial

/**
 * Created by Allan Wang on 2017-06-23.
 *
 * A holder for each individual search item Contains a [key] which acts as a unique identifier (eg
 * url) and a [content] which is displayed in the item
 */
class SearchItem(
  val key: String,
  val content: String = key,
  val description: String? = null,
  val iicon: IIcon? = GoogleMaterial.Icon.gmd_search,
  val image: Drawable? = null
) :
  KauIItem<SearchItem.ViewHolder>(
    R.layout.kau_search_iitem,
    { ViewHolder(it) },
    R.id.kau_item_search
  ) {

  companion object {
    var foregroundColor: Int = 0xdd000000.toInt()
    var backgroundColor: Int = 0xfffafafa.toInt()
  }

  private var styledContent: SpannableStringBuilder? = null

  /** Highlight the subText if it is present in the content */
  internal fun withHighlights(subText: String?) {
    subText ?: return
    val index = content.indexOf(subText, ignoreCase = true)
    if (index == -1) {
      return
    }
    styledContent = SpannableStringBuilder(content)
    styledContent!!.setSpan(
      StyleSpan(Typeface.BOLD),
      index,
      index + subText.length,
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
  }

  override fun bindView(holder: ViewHolder, payloads: List<Any>) {
    super.bindView(holder, payloads)
    holder.title.setTextColor(foregroundColor)
    holder.desc.setTextColor(foregroundColor.adjustAlpha(0.6f))

    if (image != null) {
      holder.icon.setImageDrawable(image)
    } else {
      holder.icon.setIcon(iicon, sizeDp = 18, color = foregroundColor)
    }

    holder.container.setRippleBackground(foregroundColor, backgroundColor)
    holder.title.text = styledContent ?: content
    if (description?.isNotBlank() == true) {
      holder.desc.visible().text = description
    }
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    holder.title.text = null
    holder.desc.gone().text = null
    holder.icon.setImageDrawable(null)
  }

  class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val icon: ImageView = v.findViewById(R.id.kau_search_icon)
    val title: TextView = v.findViewById(R.id.kau_search_title)
    val desc: TextView = v.findViewById(R.id.kau_search_desc)
    val container: ConstraintLayout = v.findViewById(R.id.kau_search_item_frame)
  }
}
