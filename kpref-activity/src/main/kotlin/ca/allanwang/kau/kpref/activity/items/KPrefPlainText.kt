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

import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.R

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Just text with the core options. Extends base preference but has an empty getter and setter
 * Useful replacement of [KPrefText] when nothing is displayed on the right side, and when the
 * preference is completely handled by the click
 */
open class KPrefPlainText(open val builder: KPrefPlainTextBuilder) : KPrefItemBase<Unit>(builder) {

  override fun KClick<Unit>.defaultOnClick() = Unit

  class KPrefPlainTextBuilder(globalOptions: GlobalOptions, titleId: Int) :
    BaseContract<Unit> by BaseBuilder(globalOptions, titleId, {}, {})

  override val type: Int
    get() = R.id.kau_item_pref_plain_text
}
