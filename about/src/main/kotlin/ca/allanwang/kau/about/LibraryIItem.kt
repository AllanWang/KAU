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
package ca.allanwang.kau.about

import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.startLink
import ca.allanwang.kau.utils.visible
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.select.getSelectExtension

/** Created by Allan Wang on 2017-06-27. */
class LibraryIItem(val lib: Library) :
  KauIItem<LibraryIItem.ViewHolder>(
    R.layout.kau_iitem_library,
    ::ViewHolder,
    R.id.kau_item_library
  ),
  ThemableIItem by ThemableIItemDelegate() {

  companion object {
    fun bindEvents(fastAdapter: FastAdapter<GenericItem>) {
      fastAdapter.getSelectExtension().isSelectable = true
      fastAdapter.onClickListener = { v, _, item, _ ->
        if (item !is LibraryIItem) {
          false
        } else {
          v!!.context.startLink(item.lib.website)
          true
        }
      }
    }
  }

  override var isSelectable: Boolean
    get() = false
    set(_) {}

  override fun bindView(holder: ViewHolder, payloads: List<Any>) {
    super.bindView(holder, payloads)
    with(holder) {
      name.text = lib.name
      creator.text = lib.developers.mapNotNull { it.name }.joinToString()
      @Suppress("DEPRECATION")
      description.text =
        when {
          lib.description.isNullOrBlank() -> lib.description
          Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
            Html.fromHtml(lib.description, Html.FROM_HTML_MODE_LEGACY)
          else -> Html.fromHtml(lib.description)
        }
      bottomDivider.gone()
      if (lib.artifactVersion?.isNotBlank() == true) {
        bottomDivider.visible()
        version.visible().text = lib.artifactVersion
      }
      if (lib.licenses.isNotEmpty()) {
        bottomDivider.visible()
        license.visible().text = lib.licenses.map { it.name }.sorted().joinToString()
      }
      bindTextColor(name, creator)
      bindTextColorSecondary(description)
      bindAccentColor(license, version)
      bindDividerColor(divider, bottomDivider)
      bindBackgroundRipple(card)
    }
  }

  override fun unbindView(holder: ViewHolder) {
    super.unbindView(holder)
    with(holder) {
      name.text = null
      creator.text = null
      description.text = null
      bottomDivider.gone()
      version.gone().text = null
      license.gone().text = null
    }
  }

  class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val card: CardView = v.findViewById(R.id.lib_item_card)
    val name: TextView = v.findViewById(R.id.lib_item_name)
    val creator: TextView = v.findViewById(R.id.lib_item_author)
    val description: TextView = v.findViewById(R.id.lib_item_description)
    val version: TextView = v.findViewById(R.id.lib_item_version)
    val license: TextView = v.findViewById(R.id.lib_item_license)
    val divider: View = v.findViewById(R.id.lib_item_top_divider)
    val bottomDivider: View = v.findViewById(R.id.lib_item_bottom_divider)
  }
}
