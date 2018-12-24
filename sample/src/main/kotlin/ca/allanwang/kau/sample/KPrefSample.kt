package ca.allanwang.kau.sample

import android.graphics.Color
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref

/**
 * Created by Allan Wang on 2017-06-07.
 */
object KPrefSample : KPref() {
    var version: Int  by kpref("version", -1)
    var textColor: Int by kpref("TEXT_COLOR", Color.WHITE)
    var accentColor: Int by kpref("ACCENT_COLOR", 0xffff8900.toInt())
    var bgColor: Int by kpref("BG_COLOR", 0xff303030.toInt())
    var check1: Boolean by kpref("check1", true)
    var check2: Boolean by kpref("check2", false)
    var check3: Boolean by kpref("check3", false)
    var text: String by kpref("text", "empty")
    var seekbar: Int by kpref("seekbar", 20)
    var time12: Int by kpref("time_12", 315)
    var time24: Int by kpref("time_24", 2220)
}