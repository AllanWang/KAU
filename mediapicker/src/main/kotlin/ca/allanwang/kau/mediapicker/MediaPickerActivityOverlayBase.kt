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

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import ca.allanwang.kau.utils.toast
import kotlinx.android.synthetic.main.kau_activity_image_picker_overlay.*

/**
 * Created by Allan Wang on 2017-07-23.
 *
 * Base activity for selecting images from storage
 * This variant is an overlay and selects one image only before returning directly
 * It is more efficient than [MediaPickerActivityBase], as all images are one layer deep
 * as opposed to three layers deep
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
abstract class MediaPickerActivityOverlayBase(
    mediaType: MediaType,
    mediaActions: List<MediaAction> = emptyList()
) : MediaPickerCore<MediaItemBasic>(mediaType, mediaActions) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_image_picker_overlay)
        initializeRecycler(kau_recyclerview)
        MediaItemBasic.bindEvents(this, adapter.fastAdapter)

        kau_draggable.addExitListener(this, R.transition.kau_image_exit_bottom, R.transition.kau_image_exit_top)
        kau_draggable.setOnClickListener { finishAfterTransition() }

        loadItems()
    }

    override fun finishAfterTransition() {
        kau_recyclerview.stopScroll()
        super.finishAfterTransition()
    }

    override fun onStatusChange(loaded: Boolean) {
        if (!loaded) toast(R.string.kau_no_items_loaded)
    }

    override fun converter(model: MediaModel): MediaItemBasic = MediaItemBasic(model)

    override fun onBackPressed() {
        finishAfterTransition()
    }
}
