package ca.allanwang.kau.sample

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import ca.allanwang.kau.mediapicker.*
import java.io.File

/**
 * Created by Allan Wang on 2017-07-23.
 */
private fun actions(multiple: Boolean) = listOf(object : MediaActionCamera() {

    override fun createFile(context: Context): File = createMediaFile("KAU", ".jpg")

    override fun createUri(context: Context, file: File): Uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)

}, MediaActionGallery(multiple))

class ImagePickerActivity : MediaPickerActivityBase(MediaType.IMAGE, actions(true))

class ImagePickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.IMAGE, actions(false))

class VideoPickerActivity : MediaPickerActivityBase(MediaType.VIDEO, actions(true))

class VideoPickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.VIDEO, actions(false))