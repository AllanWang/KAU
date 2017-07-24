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
fun Activity.kauLaunchImagePicker(clazz: Class<out ImagePickerCore<*>>, requestCode: Int) {
//    startActivityForResult(clazz, requestCode, true)
    startActivityForResult(clazz, requestCode, transition = ImagePickerActivityOverlayBase::class.java.isAssignableFrom(clazz))
}

/**
 * Image picker result
 * call under [Activity.onActivityResult]
 * and make sure that the requestCode matches first
 */
fun Activity.kauOnImagePickerResult(resultCode: Int, data: Intent?) = ImagePickerCore.onImagePickerResult(resultCode, data)

internal const val LOADER_ID = 42
internal const val IMAGE_PICKER_RESULT = "image_picker_result"

internal const val ANIMATION_DURATION = 200L
internal const val ANIMATION_SCALE = 0.95f

