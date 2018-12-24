package ca.allanwang.kau.mediapicker

import android.net.Uri
import android.provider.MediaStore
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * Created by Allan Wang on 2017-07-30.
 */
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
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    ),

    VIDEO(
        DiskCacheStrategy.AUTOMATIC,
        "video/*",
        MediaStore.ACTION_VIDEO_CAPTURE,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    )
}