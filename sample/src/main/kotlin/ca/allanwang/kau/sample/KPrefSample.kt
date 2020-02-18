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
package ca.allanwang.kau.sample

import android.graphics.Color
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.KPrefFactory

/**
 * Created by Allan Wang on 2017-06-07.
 */
class KPrefSample(factory: KPrefFactory) : KPref("pref_sample", factory = factory) {
    var version: Int by kpref("version", -1)
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
