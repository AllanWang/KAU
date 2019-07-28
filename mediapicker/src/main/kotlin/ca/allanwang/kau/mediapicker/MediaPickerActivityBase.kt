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

import android.database.Cursor
import android.os.Bundle
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import ca.allanwang.kau.adapters.selectedItems
import ca.allanwang.kau.adapters.selectionSize
import ca.allanwang.kau.utils.hideOnDownwardsScroll
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.toDrawable
import ca.allanwang.kau.utils.toast
import com.google.android.material.appbar.AppBarLayout
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.select.selectExtension
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import kotlinx.android.synthetic.main.kau_activity_image_picker.*

/**
 * Created by Allan Wang on 2017-07-04.
 *
 * Base activity for selecting images from storage
 * Images are blurred when selected, and multiple images can be selected at a time.
 * Having three layered images makes this slightly slower than [MediaPickerActivityOverlayBase]
 */
abstract class MediaPickerActivityBase(
    mediaType: MediaType,
    mediaActions: List<MediaAction> = emptyList()
) : MediaPickerCore<MediaItem>(mediaType, mediaActions) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.kau_activity_image_picker)

        kau_selection_count.setCompoundDrawables(
            null,
            null,
            GoogleMaterial.Icon.gmd_image.toDrawable(this, 18),
            null
        )

        setSupportActionBar(kau_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(
                GoogleMaterial.Icon.gmd_close.toDrawable(
                    this@MediaPickerActivityBase,
                    18
                )
            )
        }
        kau_toolbar.setNavigationOnClickListener { onBackPressed() }

        initializeRecycler(kau_recyclerview)

        adapter.fastAdapter!!.let {
            MediaItem.bindEvents(it)
            it.selectExtension {
                selectionListener = object : ISelectionListener<MediaItem> {
                    override fun onSelectionChanged(item: MediaItem?, selected: Boolean) {
                        kau_selection_count.text = adapter.selectionSize.toString()
                    }
                }
            }
        }

        kau_fab.apply {
            show()
            setIcon(GoogleMaterial.Icon.gmd_send)
            setOnClickListener {
                val selection = adapter.selectedItems
                if (selection.isEmpty()) {
                    toast(R.string.kau_no_items_selected)
                } else {
                    finish(ArrayList(selection.map { it.data }))
                }
            }
            hideOnDownwardsScroll(kau_recyclerview)
        }

        loadItems()
    }

    override fun converter(model: MediaModel): MediaItem = MediaItem(model)

    /**
     * Decide whether the toolbar can hide itself
     * We typically want this behaviour unless we don't have enough images
     * to fill the entire screen. In that case we don't want the recyclerview to be scrollable
     * which means the toolbar shouldn't scroll either

     * @param scrollable true if scroll flags are enabled, false otherwise
     */
    private fun setToolbarScrollable(scrollable: Boolean) {
        val params = kau_toolbar.layoutParams as AppBarLayout.LayoutParams
        if (scrollable)
            params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        else
            params.scrollFlags = 0
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        super.onLoadFinished(loader, data)
        setToolbarScrollable(
            (kau_recyclerview.layoutManager as LinearLayoutManager)
                .findLastCompletelyVisibleItemPosition() < adapter.adapterItemCount - 1
        )
    }

    override fun onStatusChange(loaded: Boolean) {
        setToolbarScrollable(loaded)
    }
}
