package ca.allanwang.kau.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextSwitcher
import android.widget.TextView

/**
 * Created by Allan Wang on 2017-06-21.
 */
class TextSwitcherThemed @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
) : TextSwitcher(context, attrs) {

    var textColor: Int = Color.WHITE
        get() = field
        set(value) {
            field = value
            (getChildAt(0) as TextView).setTextColor(value)
            (getChildAt(1) as TextView).setTextColor(value)
        }
}