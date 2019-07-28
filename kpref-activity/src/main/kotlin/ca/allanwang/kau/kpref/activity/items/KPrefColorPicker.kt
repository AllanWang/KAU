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

import ca.allanwang.kau.colorpicker.CircleView
import ca.allanwang.kau.colorpicker.ColorBuilder
import ca.allanwang.kau.colorpicker.ColorContract
import ca.allanwang.kau.colorpicker.kauColorChooser
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.KPrefItemActions
import ca.allanwang.kau.kpref.activity.R
import com.afollestad.materialdialogs.MaterialDialog

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * ColorPicker preference
 * When a color is successfully selected in the dialog, it will be saved as an int
 */
open class KPrefColorPicker(open val builder: KPrefColorContract) : KPrefItemBase<Int>(builder) {

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        if (builder.showPreview) {
            val preview = holder.bindInnerView<CircleView>(R.layout.kau_pref_color)
            preview.setBackgroundColor(pref)
            preview.withBorder = true
            builder.callback = { _, color ->
                pref = color
                if (builder.showPreview)
                    preview.setBackgroundColor(color)
                holder.updateTitle()
                holder.updateDesc()
            }
        } else {
            builder.callback = { _, color -> pref = color }
        }
    }

    override fun KClick<Int>.defaultOnClick() {
        builder.defaultColor = pref
        MaterialDialog(context).show {
            kauColorChooser(builder)
            builder.dialogBuilder(this)
            title(core.titleFun())
        }
    }

    /**
     * Extension of the base contract and [ColorContract] along with a showPreview option
     */
    interface KPrefColorContract : BaseContract<Int>, ColorContract {
        var showPreview: Boolean
        var dialogBuilder: MaterialDialog.() -> Unit
    }

    /**
     * Default implementation of [KPrefColorContract]
     */
    class KPrefColorBuilder(
        globalOptions: GlobalOptions,
        titleId: Int,
        getter: () -> Int,
        setter: KPrefItemActions.(value: Int) -> Unit
    ) : KPrefColorContract,
        BaseContract<Int> by BaseBuilder(globalOptions, titleId, getter, setter),
        ColorContract by ColorBuilder() {
        override var showPreview: Boolean = true
        override var dialogBuilder: MaterialDialog.() -> Unit = {}
    }

    override val type: Int
        get() = R.id.kau_item_pref_color_picker
}
