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
package ca.allanwang.kau.adapters

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.select.SelectExtension

/** Created by Allan Wang on 2017-11-08. */

/** Add kotlin's generic syntax to better support out types */
fun <Item : GenericItem> fastAdapter(vararg adapter: IAdapter<out Item>): FastAdapter<Item> =
    FastAdapter.with(adapter.toList())

/** Returns selection size, or -1 if selection is disabled */
inline val <Item : GenericItem> IAdapter<Item>.selectionSize: Int
  get() = fastAdapter?.getExtension<SelectExtension<Item>>()?.selections?.size ?: -1

inline val <Item : GenericItem> IAdapter<Item>.selectedItems: Set<Item>
  get() = fastAdapter?.getExtension<SelectExtension<Item>>()?.selectedItems ?: emptySet()
