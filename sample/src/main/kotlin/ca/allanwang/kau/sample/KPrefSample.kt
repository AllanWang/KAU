package ca.allanwang.kau.sample

import android.graphics.Color
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref

/**
 * Created by Allan Wang on 2017-06-07.
 */
object KPrefSample : KPref() {
    var textColor: Int by kpref("TEXT_COLOR", Color.WHITE)
    var bgColor: Int by kpref("BG_COLOR", 0xff303030.toInt())
    var check1: Boolean by kpref("check1", true)
    var check2: Boolean by kpref("check2", false)
    var check3: Boolean by kpref("check3", false)
}