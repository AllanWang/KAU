package ca.allanwang.kau.sample

import ca.allanwang.kau.mediapicker.MediaPickerActivityBase
import ca.allanwang.kau.mediapicker.MediaPickerActivityOverlayBase
import ca.allanwang.kau.mediapicker.MediaType

/**
 * Created by Allan Wang on 2017-07-23.
 */
class ImagePickerActivity : MediaPickerActivityBase(MediaType.IMAGE)

class ImagePickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.IMAGE)

class VideoPickerActivity : MediaPickerActivityBase(MediaType.VIDEO)

class VideoPickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.VIDEO)