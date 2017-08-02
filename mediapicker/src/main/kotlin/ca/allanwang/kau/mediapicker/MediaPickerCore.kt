package ca.allanwang.kau.mediapicker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.animators.FadeScaleAnimatorAdd
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.utils.dimenPixelSize
import ca.allanwang.kau.utils.toast
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable

/**
 * Created by Allan Wang on 2017-07-23.
 *
 * Container for the main logic behind the both pickers
 */
abstract class MediaPickerCore<T : IItem<*, *>>(val mediaType: MediaType) : KauBaseActivity(), LoaderManager.LoaderCallbacks<Cursor> {

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

        /**
         * Compute our resulting image size
         */
        fun computeViewSize(context: Context): Int {
            val screenWidthPx = context.resources.displayMetrics.widthPixels
            return screenWidthPx / computeColumnCount(context)
        }

        /**
         * Create error tile for a given item
         */
        fun getErrorDrawable(context: Context): Drawable {
            val sizePx = MediaPickerCore.computeViewSize(context)
            return IconicsDrawable(context, GoogleMaterial.Icon.gmd_error)
                    .sizePx(sizePx)
                    .backgroundColor(accentColor)
                    .paddingPx(sizePx / 3)
                    .color(Color.WHITE)
        }

        var accentColor: Int = 0xff666666.toInt()

        /**
         * Helper method to retrieve the media from our media picker
         * This is used for both single and multiple photo picks
         */
        fun onMediaPickerResult(resultCode: Int, data: Intent?): List<MediaModel> {
            if (resultCode != Activity.RESULT_OK || data == null || !data.hasExtra(MEDIA_PICKER_RESULT))
                return emptyList()
            return data.getParcelableArrayListExtra(MEDIA_PICKER_RESULT)
        }

        /**
         * Number of loaded items we should cache
         * This is arbitrary
         */
        const val CACHE_SIZE = 80
    }

    val adapter: FastItemAdapter<T> = FastItemAdapter()

    /**
     * Further improve preloading by extending the layout space
     */
    val extraSpace: Int by lazy { resources.displayMetrics.heightPixels }

    fun initializeRecycler(recycler: RecyclerView) {
        recycler.apply {
            val manager = object : GridLayoutManager(context, computeColumnCount(context)) {
                override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
                    return if (mediaType != MediaType.VIDEO) extraSpace else super.getExtraLayoutSpace(state)
                }
            }
            setItemViewCacheSize(CACHE_SIZE)
            isDrawingCacheEnabled = true
            layoutManager = manager
            adapter = this@MediaPickerCore.adapter
            setHasFixedSize(true)
            itemAnimator = KauAnimator(FadeScaleAnimatorAdd(0.8f))
        }
    }

    //Sort by descending date
    var sortQuery = MediaStore.MediaColumns.DATE_MODIFIED + " DESC"

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this, mediaType.contentUri, MediaModel.projection, null, null, sortQuery)
    }

    /**
     * Request read permissions and load all external items
     * The result will be filtered through {@link #onLoadFinished(Loader, Cursor)}
     * Call this to make sure that we request permissions each time
     * The adapter will be cleared on each successful call
     */
    open fun loadItems() {
        kauRequestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) {
            granted, _ ->
            if (granted) {
                supportLoaderManager.initLoader(LOADER_ID, null, this)
                onStatusChange(true)
            } else {
                toast(R.string.kau_permission_denied)
                onStatusChange(false)
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        reset()
        if (data == null || !data.moveToFirst()) {
            toast(R.string.kau_no_items_found)
            onStatusChange(false)
            return
        }
        val items = mutableListOf<T>()
        do {
            val model = MediaModel(data)
            if (!shouldLoad(model)) continue
            items.add(converter(model))
        } while (data.moveToNext())
        addItems(items)
    }

    abstract fun converter(model: MediaModel): T

    override fun onLoaderReset(loader: Loader<Cursor>?) = reset()

    /**
     * Called at the end of [onLoadFinished]
     * when the adapter should add the items
     */
    open fun addItems(items: List<T>) {
        adapter.add(items)
    }

    /**
     * Clears the adapter to prepare for a new load
     */
    open fun reset() {
        adapter.clear()
    }

    /**
     * Optional filter to decide which items get displayed
     * Defaults to checking their sizes to filter out
     * very small items such as lurking drawables/icons
     *
     * Returns true if model should be displayed, false otherwise
     */
    open fun shouldLoad(model: MediaModel): Boolean = model.size > 10000L

    open fun onStatusChange(loaded: Boolean) {}



}