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

import android.app.TimePickerDialog
import android.widget.TimePicker
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.KPrefItemActions
import ca.allanwang.kau.kpref.activity.R
import java.util.Locale

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Text preference
 * Holds a textview to display data on the right
 * This is still a generic preference
 *
 */
open class KPrefTimePicker(override val builder: KPrefTimeContract) : KPrefText<Int>(builder) {

    interface KPrefTimeContract : KPrefText.KPrefTextContract<Int>,
        TimePickerDialog.OnTimeSetListener {
        var use24HourFormat: Boolean
    }

    override fun KClick<Int>.defaultOnClick() {
        val (hour, min) = pref.splitTime
        TimePickerDialog(itemView.context, builder, hour, min, builder.use24HourFormat).show()
    }

    /**
     * Default implementation of [KPrefTimeContract]
     */
    class KPrefTimeBuilder(
        globalOptions: GlobalOptions,
        titleId: Int,
        getter: () -> Int,
        setter: KPrefItemActions.(value: Int) -> Unit
    ) : KPrefTimeContract,
        BaseContract<Int> by BaseBuilder(globalOptions, titleId, getter, setter) {

        override var use24HourFormat: Boolean = false

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            setter((hourOfDay to minute).mergeTime)
            reloadSelf()
        }

        override var textGetter: (Int) -> String? = {
            val (hour, min) = it.splitTime
            if (use24HourFormat)
                String.format(Locale.CANADA, "%d:%02d", hour, min)
            else
                String.format(
                    Locale.CANADA,
                    "%d:%02d %s",
                    hour % 12,
                    min,
                    if (hour >= 12) "PM" else "AM"
                )
        }
    }

    override val type: Int
        get() = R.id.kau_item_pref_time_picker
}

private val Int.splitTime: Pair<Int, Int>
    get() = Pair(this / 100, this % 100)

private val Pair<Int, Int>.mergeTime: Int
    get() = first * 100 + second
