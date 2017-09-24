package ca.allanwang.kau.kpref.activity.items

import android.app.TimePickerDialog
import android.view.View
import android.widget.TimePicker
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.R
import java.util.*

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Text preference
 * Holds a textview to display data on the right
 * This is still a generic preference
 *
 */
open class KPrefTimePicker(override val builder: KPrefTimeContract) : KPrefText<Int>(builder) {

    interface KPrefTimeContract : KPrefText.KPrefTextContract<Int> {
        var use24HourFormat: Boolean
    }

    /**
     * Default implementation of [KPrefTimeContract]
     */
    class KPrefTimeBuilder(
            globalOptions: GlobalOptions,
            titleRes: Int,
            getter: () -> Int,
            setter: (value: Int) -> Unit
    ) : KPrefTimeContract, BaseContract<Int> by BaseBuilder<Int>(globalOptions, titleRes, getter, setter), TimePickerDialog.OnTimeSetListener {

        override var use24HourFormat: Boolean = false

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            setter((hourOfDay to minute).mergeTime)
        }

        override var textGetter: (Int) -> String? = {
            val (hour, min) = it.splitTime
            if (use24HourFormat)
                String.format(Locale.CANADA, "%02d:%02d", hour, min)
            else
                String.format(Locale.CANADA, "%02d:%02d %s", hour % 12, min, if (hour >= 12) "PM" else "AM")
        }

        override var onClick: ((itemView: View, innerContent: View?, item: KPrefItemBase<Int>) -> Boolean)? = { itemView, _, item ->
            val (hour, min) = item.pref.splitTime
            TimePickerDialog(itemView.context, this, hour, min, use24HourFormat).show()
            true
        }

        private val Int.splitTime: Pair<Int, Int>
            get() = Pair(this / 100, this % 100)

        private val Pair<Int, Int>.mergeTime: Int
            get() = first * 100 + second
    }

    override fun getType(): Int = R.id.kau_item_pref_time_picker

}