package ca.allanwang.kau.imagepicker

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.ui.widgets.ElasticDragDismissFrameLayout
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.dimenPixelSize
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter


/**
 * Created by Allan Wang on 2017-07-04.
 *
 */
abstract class ImagePickerActivityBase : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    val toolbar: Toolbar by bindView(R.id.kau_toolbar)
    val draggableFrame: ElasticDragDismissFrameLayout by bindView(R.id.kau_draggable)
    val recycler: RecyclerView by bindView(R.id.kau_recycler)
    val imageAdapter = FastItemAdapter<ImageItem>()

    companion object {
        /**
         * Given the dimensions of our device and a minimum image size,
         * Computer the optimal column count for our grid layout
         *
         * @return column count
         */
        fun computeColumnCount(context: Context): Int {
            val minImageSizePx = context.dimenPixelSize(R.dimen.kau_image_minimum_size)
            val screenWidthPx = context.resources.displayMetrics.widthPixels
            return screenWidthPx / minImageSizePx
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_image_picker)
        recycler.layoutManager = GridLayoutManager(this, computeColumnCount(this))
        recycler.adapter = imageAdapter

        with(imageAdapter) {
            withPositionBasedStateManagement(false)
            withMultiSelect(true)
            withSelectable(true)
            withOnClickListener { v, _, _, _ ->
                (v as BlurredImageView).toggleBlur()
                true
            }
        }
        draggableFrame.addListener(object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                if (draggableFrame.translationY < 0) {
//                    window.returnTransition = TransitionInflater.from(this@ImagePickerActivityBase)
//                            .inflateTransition(R.transition.kau_about_return_upwards)
                }
                finishAfterTransition()
            }
        })
        kauRequestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) {
            granted, _ ->
            if (granted) {
                supportLoaderManager.initLoader(42, null, this)
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val columns = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        )
        return CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        reset()
        if (data == null) return
        if (data.moveToFirst()) {
            do {
                val img = ImageModel(data)
                imageAdapter.add(ImageItem(img))
            } while (data.moveToNext())
        }
    }

    private fun reset() {
        imageAdapter.clear();
    }

    override fun onLoaderReset(loader: Loader<Cursor>) = reset()
}