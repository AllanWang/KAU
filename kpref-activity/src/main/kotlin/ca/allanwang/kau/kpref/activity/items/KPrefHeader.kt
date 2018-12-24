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
package ca.allanwang.kau.kpref.activity.items

import ca.allanwang.kau.kpref.activity.R

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Header preference
 * This view just holds a title and is not clickable. It is styled using the accent color
 */
open class KPrefHeader(builder: CoreContract) : KPrefItemCore(builder) {

    override fun getLayoutRes(): Int = R.layout.kau_pref_header

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        withAccentColor(holder.title::setTextColor)
    }

    override fun getType() = R.id.kau_item_pref_header
}
