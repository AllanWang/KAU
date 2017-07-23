package ca.allanwang.kau.imagepicker

import android.app.Activity
import android.content.Intent

/**
 * Created by Allan Wang on 2017-07-21.
 *
 * Extension functions for interacting with the image picker
 * as well as internal constants
 */

/**
 * Image picker launcher
 */
fun Activity.kauLaunchImagePicker(clazz: Class<out ImagePickerActivityBase>, requestCode: Int) {
    startActivityForResult(Intent(this, clazz), requestCode)
}

/**
 * Image picker result
 * call under [Activity.onActivityResult]
 * and make sure that the requestCode matches first
 */
fun Activity.kauOnImagePickerResult(resultCode: Int, data: Intent?) = ImagePickerActivityBase.onImagePickerResult(resultCode, data)

internal const val LOADER_ID = 42
internal const val IMAGE_PICKER_RESULT = "image_picker_result"

internal const val ANIMATION_DURATION = 200L
internal const val ANIMATION_SCALE = 0.95f

