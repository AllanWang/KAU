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

import android.net.Uri
import android.provider.MediaStore
import com.bumptech.glide.load.engine.DiskCacheStrategy

/** Created by Allan Wang on 2017-07-30. */
enum class MediaType(
    val cacheStrategy: DiskCacheStrategy,
    val mimeType: String,
    val captureType: String,
    val contentUri: Uri
) {
  IMAGE(
      DiskCacheStrategy.AUTOMATIC,
      "image/*",
      MediaStore.ACTION_IMAGE_CAPTURE,
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
  VIDEO(
      DiskCacheStrategy.AUTOMATIC,
      "video/*",
      MediaStore.ACTION_VIDEO_CAPTURE,
      MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
}
