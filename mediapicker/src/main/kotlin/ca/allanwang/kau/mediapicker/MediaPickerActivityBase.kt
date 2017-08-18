package ca.allanwang.kau.mediapicker

import android.database.Cursor
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.TextView
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial

/**
 * Created by Allan Wang on 2017-07-04.
 *
 * Base activity for selecting images from storage
 * Images are blurred when selected, and multiple images can be selected at a time.
 * Having three layered images makes this slightly slower than [MediaPickerActivityOverlayBase]
 */
abstract class MediaPickerActivityBase(
        mediaType: MediaType,
        mediaActions: List<MediaActionItem> = emptyList()
) : MediaPickerCore<MediaItem>(mediaType, mediaActions) {

    val coordinator: CoordinatorLayout by bindView(R.id.kau_coordinator)
    val toolbar: Toolbar by bindView(R.id.kau_toolbar)
    val selectionCount: TextView by bindView(R.id.kau_selection_count)
    val recycler: RecyclerView by bindView(R.id.kau_recyclerview)
    val fab: FloatingActionButton by bindView(R.id.kau_fab)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.kau_activity_image_picker)

        selectionCount.setCompoundDrawables(null, null, GoogleMaterial.Icon.gmd_image.toDrawable(this, 18), null)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(GoogleMaterial.Icon.gmd_close.toDrawable(this@MediaPickerActivityBase, 18))
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }

        initializeRecycler(recycler)

        MediaItem.bindEvents(adapter)
        adapter.withSelectionListener({ _, _ -> selectionCount.text = adapter.selections.size.toString() })

        fab.apply {
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
            hideOnDownwardsScroll(recycler)
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
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        if (scrollable)
            params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        else
            params.scrollFlags = 0
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        super.onLoadFinished(loader, data)
        setToolbarScrollable((recycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1)
    }

    override fun onStatusChange(loaded: Boolean) {
        setToolbarScrollable(loaded)
    }

}