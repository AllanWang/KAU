package ca.allanwang.kau.imagepicker

import android.app.Activity
import android.content.Intent
import ca.allanwang.kau.utils.startActivityForResult

/**
 * Created by Allan Wang on 2017-07-21.
 *
 * Extension functions for interacting with the image picker
 * as well as internal constants
 */

/**
 * Image picker launchers
 */
fun Activity.kauLaunchMediaPicker(clazz: Class<out MediaPickerCore<*>>, requestCode: Int) {
    startActivityForResult(clazz, requestCode, transition = MediaPickerActivityOverlayBase::class.java.isAssignableFrom(clazz))
}

/**
 * Image picker result
 * call under [Activity.onActivityResult]
 * and make sure that the requestCode matches first
 */
fun Activity.kauOnMediaPickerResult(resultCode: Int, data: Intent?) = MediaPickerCore.onMediaPickerResult(resultCode, data)

internal const val LOADER_ID = 42
internal const val MEDIA_PICKER_RESULT = "media_picker_result"

internal const val ANIMATION_DURATION = 200L
internal const val ANIMATION_SCALE = 0.95f

