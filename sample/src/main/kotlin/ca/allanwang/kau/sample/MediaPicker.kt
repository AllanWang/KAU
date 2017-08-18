package ca.allanwang.kau.sample

import ca.allanwang.kau.mediapicker.*

/**
 * Created by Allan Wang on 2017-07-23.
 */
class ImagePickerActivity : MediaPickerActivityBase(MediaType.IMAGE,
        mediaActions = listOf(MediaActionItem(MediaActionGallery(true))))

class ImagePickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.IMAGE)

class VideoPickerActivity : MediaPickerActivityBase(MediaType.VIDEO)

class VideoPickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.VIDEO)