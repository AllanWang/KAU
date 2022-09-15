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

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.ui.views.CutoutView

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Just a cutout item with some defaults in [R.layout.kau_iitem_cutout]
 */
class CutoutIItem(val config: CutoutView.() -> Unit = {}) :
    KauIItem<CutoutIItem.ViewHolder>(R.layout.kau_iitem_cutout, ::ViewHolder, R.id.kau_item_cutout),
    ThemableIItem by ThemableIItemDelegate() {

  override var isSelectable: Boolean
    get() = false
    set(_) {}

  override fun bindView(holder: ViewHolder, payloads: List<Any>) {
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
      cutout.text = "Text" // back to default
    }
  }

  class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val cutout: CutoutView = v.findViewById(R.id.kau_cutout)
  }
}
