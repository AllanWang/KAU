package ca.allanwang.kau.imagepicker

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.ui.widgets.ElasticDragDismissFrameLayout
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.toast

/**
 * Created by Allan Wang on 2017-07-23.
 *
 * Base activity for selecting images from storage
 * This variant is an overlay and selects one image only before returning directly
 * It is more efficient than [ImagePickerActivityBase], as all images are one layer deep
 * as opposed to three layers deep
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
abstract class ImagePickerActivityOverlayBase : ImagePickerCore<ImageItemBasic>() {

    val draggable: ElasticDragDismissFrameLayout by bindView(R.id.kau_draggable)
    val recycler: RecyclerView by bindView(R.id.kau_recyclerview)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_image_picker_overlay)
        initializeRecycler(recycler)
        ImageItemBasic.bindEvents(this, adapter)

        draggable.addExitListener(this, R.transition.kau_image_exit_bottom, R.transition.kau_image_exit_top)
        draggable.setOnClickListener { finishAfterTransition() }

        loadItems()
    }

    override fun finishAfterTransition() {
        recycler.stopScroll()
        super.finishAfterTransition()
    }

    override fun onStatusChange(loaded: Boolean) {
        if (!loaded) toast(R.string.kau_no_images_loaded)
    }

    override fun converter(model: ImageModel): ImageItemBasic = ImageItemBasic(model)

    override fun onBackPressed() {
        finishAfterTransition()
    }
}