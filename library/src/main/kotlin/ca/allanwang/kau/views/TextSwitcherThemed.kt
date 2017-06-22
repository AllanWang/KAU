package ca.allanwang.kau.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextSwitcher
import android.widget.TextView

/**
 * Created by Allan Wang on 2017-06-21.
 */
class TextSwitcherThemed @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
) : TextSwitcher(context, attrs) {

    var textColor: Int = -1
        get() = field
        set(value) {
            field = value
            if (value != -1) {
                (getChildAt(0) as TextView).setTextColor(value)
                (getChildAt(1) as TextView).setTextColor(value)
            }
        }
}