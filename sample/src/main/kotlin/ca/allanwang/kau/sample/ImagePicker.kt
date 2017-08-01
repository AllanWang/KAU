package ca.allanwang.kau.sample

<<<<<<< HEAD
import ca.allanwang.kau.mediapicker.MediaPickerActivityBase
import ca.allanwang.kau.mediapicker.MediaPickerActivityOverlayBase
import ca.allanwang.kau.mediapicker.MediaType
=======
import ca.allanwang.kau.imagepicker.MediaPickerActivityBase
import ca.allanwang.kau.imagepicker.MediaPickerActivityOverlayBase
import ca.allanwang.kau.imagepicker.MediaType
>>>>>>> master

/**
 * Created by Allan Wang on 2017-07-23.
 */
class ImagePickerActivity : MediaPickerActivityBase(MediaType.IMAGE)

class ImagePickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.IMAGE)

class VideoPickerActivity : MediaPickerActivityBase(MediaType.VIDEO)

class VideoPickerActivityOverlay : MediaPickerActivityOverlayBase(MediaType.VIDEO)