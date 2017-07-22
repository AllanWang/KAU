package ca.allanwang.kau.imagepicker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.widget.TextView
import ca.allanwang.kau.animators.FadeScaleAnimatorAdd
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial


/**
 * Created by Allan Wang on 2017-07-04.
 *
 * Base activity for selecting images from storage
 */
abstract class ImagePickerActivityBase : ElasticRecyclerActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    val imageAdapter = FastItemAdapter<ImageItem>()
    lateinit var selectionCount: TextView

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

        var accentColor: Int = 0xff666666.toInt()

        fun onImagePickerResult(resultCode: Int, data: Intent?): List<ImageModel> {
            if (resultCode != Activity.RESULT_OK || data == null || !data.hasExtra(IMAGE_PICKER_RESULT))
                return emptyList()
            return data.getParcelableArrayListExtra(IMAGE_PICKER_RESULT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {

        selectionCount = TextView(this)

        with(selectionCount) {
            layoutParams = Toolbar.LayoutParams(
                    Toolbar.LayoutParams.WRAP_CONTENT,
                    Toolbar.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL or Gravity.END
            )
            setPaddingHorizontal(dimenPixelSize(R.dimen.kau_padding_normal))
            compoundDrawablePadding = dimenPixelSize(R.dimen.kau_padding_small)
            setCompoundDrawables(null, null, GoogleMaterial.Icon.gmd_image.toDrawable(context, 18), null)
            text = "0"
        }

        toolbar.addView(selectionCount)
        toolbar.setOnClickListener { recycler.scrollToPosition(0) }

        with(recycler) {
            layoutManager = GridLayoutManager(context, computeColumnCount(context))
            adapter = imageAdapter
            setHasFixedSize(true)
            itemAnimator = KauAnimator(FadeScaleAnimatorAdd(0.8f))
        }

        ImageItem.bindEvents(imageAdapter)
        imageAdapter.withSelectionListener({ _, _ -> selectionCount.text = imageAdapter.selections.size.toString() })

        with(fab) {
            show()
            setIcon(GoogleMaterial.Icon.gmd_send)
            setOnClickListener {
                val selection = imageAdapter.selectedItems
                if (selection.isEmpty()) {
                    toast(R.string.kau_no_images_selected)
                } else {
                    val intent = Intent()
                    val data = ArrayList(selection.map { it.data })
                    intent.putParcelableArrayListExtra(IMAGE_PICKER_RESULT, data)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
        hideFabOnUpwardsScroll()

        //If we have a transition, finish it first before loading images
        window.enterTransition?.addEndListener { loadImages() } ?: loadImages()

        return true
    }

    /**
     * Request read permissions and load all external images
     * The result will be filtered through {@link #onLoadFinished(Loader, Cursor)}
     * Call this to make sure that we request permissions each time
     * The adapter will be cleared on each successful call
     */
    private fun loadImages() {
        kauRequestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) {
            granted, _ ->
            if (granted) supportLoaderManager.initLoader(LOADER_ID, null, this)
            else toast(R.string.kau_permission_denied)
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
                //Sort by descending date
                MediaStore.Images.Media.DATE_MODIFIED + " DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        reset()
        if (data == null || !data.moveToFirst()) {
            toast(R.string.kau_no_images_found)
            return
        }
        do {
            val model = ImageModel(data)
            if (!shouldLoad(model)) continue
            imageAdapter.add(ImageItem(model))
        } while (data.moveToNext())
    }

    /**
     * Optional filter to decide which images get displayed
     * Defaults to checking their sizes to filter out
     * very small images such as lurking drawables/icons
     *
     * Returns true if model should be displayed, false otherwise
     */
    open fun shouldLoad(model: ImageModel): Boolean = model.size > 10000L

    private fun reset() {
        imageAdapter.clear();
    }

    override fun onLoaderReset(loader: Loader<Cursor>) = reset()

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }
}