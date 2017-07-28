package ca.allanwang.kau.utils

/**
 * Created by Allan Wang on 2017-06-08.
 */
const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"

const val KAU_LEFT = 1
const val KAU_TOP = 2
const val KAU_RIGHT = 4
const val KAU_BOTTOM = 8
const val KAU_HORIZONTAL = KAU_LEFT or KAU_RIGHT
const val KAU_VERTICAL = KAU_TOP or KAU_BOTTOM
const val KAU_ALL = KAU_HORIZONTAL or KAU_VERTICAL