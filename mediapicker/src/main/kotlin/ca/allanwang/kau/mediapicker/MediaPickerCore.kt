/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.mediapicker

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.adapters.fastAdapter
import ca.allanwang.kau.animators.FadeScaleAnimatorAdd
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.kotlin.lazyContext
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.utils.dimenPixelSize
import ca.allanwang.kau.utils.toast
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize
import com.mikepenz.iconics.colorInt
import com.mikepenz.iconics.paddingPx
import com.mikepenz.iconics.sizePx
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.iconics.utils.toIconicsColor
import com.mikepenz.iconics.utils.toIconicsSizePx
import kotlinx.coroutines.CancellationException
import java.io.File

/**
 * Created by Allan Wang on 2017-07-23.
 *
 * Container for the main logic behind the both pickers
 */
abstract class MediaPickerCore<T : IItem<*>>(
    val mediaType: MediaType,
    val mediaActions: List<MediaAction>
) : KauBaseActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        val viewSize = lazyContext { computeViewSize(it) }
        /**
         * Given the dimensions of our device and a minimum image size,
         * Computer the optimal column count for our grid layout
         *
         * @return column count
         */
        private fun computeColumnCount(context: Context): Int {
            val minImageSizePx = context.dimenPixelSize(R.dimen.kau_image_minimum_size)
            val screenWidthPx = context.resources.displayMetrics.widthPixels
            return screenWidthPx / minImageSizePx
        }

        /**
         * Compute our resulting image size
         */
        private fun computeViewSize(context: Context): Int {
            val screenWidthPx = context.resources.displayMetrics.widthPixels
            return screenWidthPx / computeColumnCount(context)
        }

        /**
         * Create error tile for a given item
         */
        fun getErrorDrawable(context: Context) = getIconDrawable(context, GoogleMaterial.Icon.gmd_error, accentColor)

        fun getIconDrawable(context: Context, iicon: IIcon, color: Int): Drawable {
            val sizePx = MediaPickerCore.computeViewSize(context)
            return IconicsDrawable(context, iicon)
                .sizePx(sizePx)
                .backgroundColor(color.toIconicsColor())
                .paddingPx(sizePx / 3)
                .colorInt(Color.WHITE)
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

    lateinit var glide: RequestManager
    private var hasPreloaded = false

    val adapter = ItemAdapter<T>()

    /**
     * Further improve preloading by extending the layout space
     */
    val extraSpace: Int by lazy { resources.displayMetrics.heightPixels }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glide = Glide.with(this)
    }

    fun initializeRecycler(recycler: RecyclerView) {
        val adapterHeader = ItemAdapter<MediaActionItem>()
        val fulladapter = fastAdapter<IItem<*>>(adapterHeader, adapter)
        adapterHeader.add(mediaActions.map { MediaActionItem(it, mediaType) })
        recycler.apply {
            val manager = object : GridLayoutManager(context, computeColumnCount(context)) {
                override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
                    return if (mediaType != MediaType.VIDEO) extraSpace else super.getExtraLayoutSpace(state)
                }
            }
            setItemViewCacheSize(CACHE_SIZE)
            layoutManager = manager
            adapter = fulladapter
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
        kauRequestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) { granted, _ ->
            if (granted) {
                LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
                onStatusChange(true)
            } else {
                toast(R.string.kau_permission_denied)
                onStatusChange(false)
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        reset()
        if (data == null || !data.moveToFirst()) {
            toast(R.string.kau_no_items_found)
            onStatusChange(false)
            return
        }
        val models = mutableListOf<MediaModel>()
        do {
            val model = MediaModel(data)
            if (!shouldLoad(model)) continue
            models.add(model)
        } while (data.moveToNext())
        addItems(models.map { converter(it) })
        if (!hasPreloaded && mediaType == MediaType.VIDEO) {
            hasPreloaded = true
            val preloads = models.subList(0, Math.min(models.size, 50)).map {
                glide.load(it.data)
                    .applyMediaOptions(this@MediaPickerCore)
                    .preload()
            }
            job.invokeOnCompletion {
                if (it is CancellationException) {
                    preloads.forEach(glide::clear)
                }
            }
        }
    }

    abstract fun converter(model: MediaModel): T

    override fun onLoaderReset(loader: Loader<Cursor>) = reset()

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

    /**
     * Method used to retrieve uri data for API 19+
     * See <a href="http://hmkcode.com/android-display-selected-image-and-its-real-path/"></a>
     */
    private fun <R> ContentResolver.query(baseUri: Uri, uris: List<Uri>, block: (cursor: Cursor) -> R) {
        val ids = uris.filter {
            val valid = DocumentsContract.isDocumentUri(this@MediaPickerCore, it)
            if (!valid) KL.d { "Non document uri: ${it.encodedPath}" }
            valid
        }.mapNotNull {
            DocumentsContract.getDocumentId(it).split(":").getOrNull(1)
        }.joinToString(prefix = "(", separator = ",", postfix = ")")
        //? query replacements are done for one arg at a time
        //since we potentially have a list of ids, we'll just format the WHERE clause ourself
        query(baseUri, MediaModel.projection, "${BaseColumns._ID} IN $ids", null, sortQuery)?.use(block)
    }

    internal var tempPath: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            if (tempPath != null) {
                val f = File(tempPath)
                if (f.exists()) f.delete()
                tempPath = null
            }
            return super.onActivityResult(requestCode, resultCode, data)
        }
        KL.d { "Media result received" }
        when (requestCode) {
            MEDIA_ACTION_REQUEST_CAMERA -> onCameraResult(data)
            MEDIA_ACTION_REQUEST_PICKER -> onPickerResult(data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onCameraResult(data: Intent?) {
        val f: File
        if (tempPath != null) {
            f = File(tempPath)
            tempPath = null
        } else if (data?.data != null) {
            f = File(data.data!!.path)
        } else {
            KL.d { "Media camera no file found" }
            return
        }
        if (f.exists()) {
            KL.v { "Media camera path found: ${f.absolutePath}" }
            scanMedia(f)
            finish(arrayListOf(MediaModel(f)))
        } else {
            KL.d { "Media camera file not found" }
        }
    }

    private fun onPickerResult(data: Intent?) {
        val items = mutableListOf<Uri>()
        if (data?.data != null) {
            KL.v { "Media picker data uri: ${data.data!!.path}" }
            items.add(data.data!!)
        } else if (data != null) {
            val clip = data.clipData
            if (clip != null) {
                items.addAll((0 until clip.itemCount).map {
                    clip.getItemAt(it).uri.apply {
                        KL.v { "Media picker clip uri $path" }
                    }
                })
            }
        }
        if (items.isEmpty()) return KL.d { "Media picker empty intent" }
        contentResolver.query(mediaType.contentUri, items) {
            if (it.moveToFirst()) {
                val models = arrayListOf<MediaModel>()
                do {
                    models.add(MediaModel(it))
                } while (it.moveToNext())
                finish(models)
            }
        }
    }
}
