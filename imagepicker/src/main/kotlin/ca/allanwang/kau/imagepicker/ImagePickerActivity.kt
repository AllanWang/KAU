package ca.allanwang.kau.imagepicker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.TextView
import ca.allanwang.kau.animators.FadeScaleAnimatorAdd
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial


/**
 * Created by Allan Wang on 2017-07-04.
 *
 * Base activity for selecting images from storage
 */
open class ImagePickerActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    val imageAdapter = FastItemAdapter<ImageItem>()

    val coordinator: CoordinatorLayout by bindView(R.id.kau_coordinator)
    val toolbar: Toolbar by bindView(R.id.kau_toolbar)
    val selectionCount: TextView by bindView(R.id.kau_selection_count)
    val recycler: RecyclerView by bindView(R.id.kau_recyclerview)
    val fab: FloatingActionButton by bindView(R.id.kau_fab)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.kau_activity_image_picker)

        selectionCount.setCompoundDrawables(null, null, GoogleMaterial.Icon.gmd_image.toDrawable(this, 18), null)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(GoogleMaterial.Icon.gmd_close.toDrawable(this@ImagePickerActivity, 18))
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recycler.apply {
            layoutManager = GridLayoutManager(context, computeColumnCount(context))
            adapter = imageAdapter
            setHasFixedSize(true)
            itemAnimator = KauAnimator(FadeScaleAnimatorAdd(0.8f))
        }

        ImageItem.bindEvents(imageAdapter)
        imageAdapter.withSelectionListener({ _, _ -> selectionCount.text = imageAdapter.selections.size.toString() })

        fab.apply {
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
            hideOnDownwardsScroll(recycler)
        }

        loadImages()
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
            if (granted) {
                supportLoaderManager.initLoader(LOADER_ID, null, this)
                setToolbarScrollable(true)
            } else {
                toast(R.string.kau_permission_denied)
                setToolbarScrollable(false)
            }
        }
    }

    /**
     * Decide whether the toolbar can hide itself
     * We typically want this behaviour unless we don't have enough images
     * to fill the entire screen. In that case we don't want the recyclerview to be scrollable
     * which means the toolbar shouldn't scroll either

     * @param scrollable true if scroll flags are enabled, false otherwise
     */
    private fun setToolbarScrollable(scrollable: Boolean) {
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        if (scrollable)
            params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        else
            params.scrollFlags = 0
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
            setToolbarScrollable(false)
            return
        }
        do {
            val model = ImageModel(data)
            if (!shouldLoad(model)) continue
            imageAdapter.add(ImageItem(model))
        } while (data.moveToNext())
        setToolbarScrollable((recycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() < imageAdapter.getItemCount() - 1)
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