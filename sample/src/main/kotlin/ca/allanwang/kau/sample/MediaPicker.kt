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
package ca.allanwang.kau.sample

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import ca.allanwang.kau.mediapicker.MediaActionCamera
import ca.allanwang.kau.mediapicker.MediaActionGallery
import ca.allanwang.kau.mediapicker.MediaPickerActivityBase
import ca.allanwang.kau.mediapicker.MediaPickerActivityOverlayBase
import ca.allanwang.kau.mediapicker.MediaType
import ca.allanwang.kau.mediapicker.createMediaFile
import java.io.File

/** Created by Allan Wang on 2017-07-23. */
private fun actions(multiple: Boolean) =
    listOf(
        object : MediaActionCamera() {

          override fun createFile(context: Context): File = createMediaFile("KAU", ".jpg")

          override fun createUri(context: Context, file: File): Uri =
              FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
        },
        MediaActionGallery(multiple))

class ImagePickerActivity : MediaPickerActivityBase(MediaType.IMAGE, actions(true))

class ImagePickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.IMAGE, actions(false))

class VideoPickerActivity : MediaPickerActivityBase(MediaType.VIDEO, actions(true))

class VideoPickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.VIDEO, actions(false))
