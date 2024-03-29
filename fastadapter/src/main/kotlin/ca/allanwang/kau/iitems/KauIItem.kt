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
package ca.allanwang.kau.iitems

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * Created by Allan Wang on 2017-07-03.
 *
 * Kotlin implementation of the [AbstractItem] to make things shorter If only one iitem type extends
 * the given [layoutRes], you may use it as the type and not worry about another id
 */
open class KauIItem<VH : RecyclerView.ViewHolder>(
  @param:LayoutRes override val layoutRes: Int,
  private val viewHolder: (v: View) -> VH,
  override val type: Int = layoutRes
) : AbstractItem<VH>() {
  final override fun getViewHolder(v: View): VH = viewHolder(v)
}
