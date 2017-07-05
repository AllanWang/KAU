package ca.allanwang.kau.imagepicker

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
import android.transition.TransitionInflater
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.widgets.ElasticDragDismissFrameLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_image_picker)
        with(recycler) {
            layoutManager = GridLayoutManager(this@ImagePickerActivityBase, 3)
            adapter = this@ImagePickerActivityBase.imageAdapter
        }
        imageAdapter.add(arrayOf("a", "b", "c").map { ImageItem(it) })
        draggableFrame.addListener(object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                if (draggableFrame.translationY < 0) {
                    window.returnTransition = TransitionInflater.from(this@ImagePickerActivityBase)
                            .inflateTransition(R.transition.kau_about_return_upwards)
                }
                finishAfterTransition()
            }
        })
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val columns = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED)

        return CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        val dataIndex = data.getColumnIndex(MediaStore.Images.Media.DATA)
        val alstPhotos = mutableListOf<String>()

        data.moveToLast()
        while (!data.isBeforeFirst) {
            val photoPath = data.getString(dataIndex)
            KL.d(photoPath)
            alstPhotos.add(photoPath)
            data.moveToPrevious()
        }
        imageAdapter.add(alstPhotos.map { ImageItem(it) })
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        imageAdapter.clear()
    }
}