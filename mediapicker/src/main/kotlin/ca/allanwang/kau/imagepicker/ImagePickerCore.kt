package ca.allanwang.kau.imagepicker

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import ca.allanwang.kau.utils.toast
import com.mikepenz.fastadapter.IItem

/**
 * Created by Allan Wang on 2017-07-23.
 *
 * Container for the main logic behind the image pickers
 */
abstract class ImagePickerCore<T : IItem<*, *>> : PickerCore<T, ImageModel>() {

    companion object {
        /**
         * Helper method to retrieve the images from our iamge picker
         * This is used for both single and multiple photo picks
         */
        fun onImagePickerResult(resultCode: Int, data: Intent?): List<ImageModel> {
            if (resultCode != Activity.RESULT_OK || data == null || !data.hasExtra(IMAGE_PICKER_RESULT))
                return emptyList()
            return data.getParcelableArrayListExtra(IMAGE_PICKER_RESULT)
        }
    }

    //Sort by descending date
    var sortQuery = MediaStore.Images.Media.DATE_MODIFIED + " DESC"

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val columns = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        )

        return CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, sortQuery)
    }

    override fun factory(data: Cursor): ImageModel = ImageModel(data)

    override fun onLoaderReset(loader: Loader<Cursor>?) = reset()

    /**
     * Optional filter to decide which images get displayed
     * Defaults to checking their sizes to filter out
     * very small images such as lurking drawables/icons
     *
     * Returns true if model should be displayed, false otherwise
     */
    override fun shouldLoad(model: ImageModel): Boolean = model.size > 10000L

}