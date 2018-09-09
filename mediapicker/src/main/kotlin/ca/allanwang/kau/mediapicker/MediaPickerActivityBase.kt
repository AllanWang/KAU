package ca.allanwang.kau.mediapicker

import android.database.Cursor
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import ca.allanwang.kau.adapters.selectedItems
import ca.allanwang.kau.adapters.selectionSize
import ca.allanwang.kau.utils.hideOnDownwardsScroll
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.toDrawable
import ca.allanwang.kau.utils.toast
import com.mikepenz.google_material_typeface_library.GoogleMaterial
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

        kau_selection_count.setCompoundDrawables(null, null, GoogleMaterial.Icon.gmd_image.toDrawable(this, 18), null)

        setSupportActionBar(kau_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(GoogleMaterial.Icon.gmd_close.toDrawable(this@MediaPickerActivityBase, 18))
        }
        kau_toolbar.setNavigationOnClickListener { onBackPressed() }

        initializeRecycler(kau_recyclerview)

        MediaItem.bindEvents(adapter.fastAdapter)
        adapter.fastAdapter.withSelectionListener { _, _ ->
            kau_selection_count.text = adapter.selectionSize.toString()
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
            params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        else
            params.scrollFlags = 0
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        super.onLoadFinished(loader, data)
        setToolbarScrollable((kau_recyclerview.layoutManager as LinearLayoutManager)
                .findLastCompletelyVisibleItemPosition() < adapter.adapterItemCount - 1)
    }

    override fun onStatusChange(loaded: Boolean) {
        setToolbarScrollable(loaded)
    }

}