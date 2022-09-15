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

import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.R
import ca.allanwang.kau.utils.tint

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Checkbox preference When clicked, will toggle the preference and the apply the result to the
 * checkbox
 */
open class KPrefCheckbox(builder: BaseContract<Boolean>) : KPrefItemBase<Boolean>(builder) {

  override fun KClick<Boolean>.defaultOnClick() {
    pref = !pref
    (innerView as AppCompatCheckBox).isChecked = pref
  }

  override fun bindView(holder: ViewHolder, payloads: List<Any>) {
    super.bindView(holder, payloads)
    val checkbox = holder.bindInnerView<CheckBox>(R.layout.kau_pref_checkbox)
    withAccentColor(checkbox::tint)
    checkbox.isChecked = pref
    checkbox.jumpDrawablesToCurrentState() // Cancel the animation
  }

  override val type: Int
    get() = R.id.kau_item_pref_checkbox
}
