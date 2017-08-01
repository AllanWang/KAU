package ca.allanwang.kau.imagepicker

import android.net.Uri
import android.provider.MediaStore
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * Created by Allan Wang on 2017-07-30.
 */
enum class MediaType(val cacheStrategy: DiskCacheStrategy, val contentUri: Uri) {
    IMAGE(DiskCacheStrategy.AUTOMATIC, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
    VIDEO(DiskCacheStrategy.AUTOMATIC, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
}