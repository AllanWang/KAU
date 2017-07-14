package ca.allanwang.kau.imagepicker

import android.database.Cursor
import android.provider.MediaStore
import android.support.annotation.NonNull


/**
 * Created by Allan Wang on 2017-07-14.
 */
class ImageModel(@NonNull cursor: Cursor) {

    val size = cursor.getLong(MediaStore.Images.Media.SIZE)
    val dateModified = cursor.getLong(MediaStore.Images.Media.DATE_MODIFIED)
    val data = cursor.getString(MediaStore.Images.Media.DATA)
    val displayName = cursor.getString(MediaStore.Images.Media.DISPLAY_NAME)

    private fun Cursor.getString(name: String) = getString(getColumnIndex(name))
    private fun Cursor.getLong(name: String) = getLong(getColumnIndex(name))

}