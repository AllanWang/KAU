package ca.allanwang.kau.utils

import android.content.Context
import android.support.annotation.StringRes

/**
 * Created by Allan Wang on 2017-06-08.
 */
class StringHolder {
    var text: String? = null
    var textRes: Int = 0

    constructor(@StringRes textRes: Int) {
        this.textRes = textRes
    }

    constructor(text: String) {
        this.text = text
    }

    fun getString(context: Context) = context.string(textRes, text)
}