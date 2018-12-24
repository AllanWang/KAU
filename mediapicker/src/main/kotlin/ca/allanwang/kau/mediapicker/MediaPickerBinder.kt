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

import android.app.Activity
import android.content.Context
import android.content.Intent
import ca.allanwang.kau.utils.startActivityForResult
import ca.allanwang.kau.utils.withSceneTransitionAnimation
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * Created by Allan Wang on 2017-07-21.
 *
 * Extension functions for interacting with the image picker
 * as well as internal constants
 */

/**
 * Image picker launchers
 */
inline fun <reified T : MediaPickerCore<*>> Activity.kauLaunchMediaPicker(requestCode: Int) {
    startActivityForResult<T>(requestCode, bundleBuilder = {
        if (MediaPickerActivityOverlayBase::class.java.isAssignableFrom(T::class.java))
            withSceneTransitionAnimation(this@kauLaunchMediaPicker)
    })
}

/**
 * Image picker result
 * call under [Activity.onActivityResult]
 * and make sure that the requestCode matches first
 */
fun Activity.kauOnMediaPickerResult(resultCode: Int, data: Intent?) =
    MediaPickerCore.onMediaPickerResult(resultCode, data)

internal const val LOADER_ID = 42
internal const val MEDIA_PICKER_RESULT = "media_picker_result"

internal const val ANIMATION_DURATION = 200L
internal const val ANIMATION_SCALE = 0.95f

internal fun <T> RequestBuilder<T>.applyMediaOptions(context: Context) = apply(
    RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop().override(
        MediaPickerCore.viewSize(context)
    )
)
