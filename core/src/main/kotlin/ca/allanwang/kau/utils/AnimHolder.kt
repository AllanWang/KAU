package ca.allanwang.kau.utils

import ca.allanwang.kau.kotlin.lazyInterpolator

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Holder for a bunch of common animators/interpolators used throughout this library
 */
object AnimHolder {

    val fastOutSlowInInterpolator = lazyInterpolator(android.R.interpolator.fast_out_linear_in)
    val decelerateInterpolator = lazyInterpolator(android.R.interpolator.decelerate_cubic)

}