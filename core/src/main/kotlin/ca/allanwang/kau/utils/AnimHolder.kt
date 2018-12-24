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
package ca.allanwang.kau.utils

import android.os.Build
import androidx.annotation.RequiresApi
import ca.allanwang.kau.kotlin.lazyInterpolator

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Holder for a bunch of common animators/interpolators used throughout this library
 */
object AnimHolder {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val fastOutSlowInInterpolator = lazyInterpolator(android.R.interpolator.fast_out_linear_in)
    val decelerateInterpolator = lazyInterpolator(android.R.interpolator.decelerate_cubic)
}
