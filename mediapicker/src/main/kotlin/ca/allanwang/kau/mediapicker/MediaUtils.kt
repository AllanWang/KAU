package ca.allanwang.kau.mediapicker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import ca.allanwang.kau.utils.buildIsLollipopAndUp
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Allan Wang on 2017-08-17.
 */
@SuppressLint("NewApi")
internal fun Activity.finish(data: ArrayList<MediaModel>) {
    val intent = Intent()
    intent.putParcelableArrayListExtra(MEDIA_PICKER_RESULT, data)
    setResult(AppCompatActivity.RESULT_OK, intent)
    if (buildIsLollipopAndUp) finishAfterTransition()
    else finish()
}

@Throws(IOException::class)
fun createMediaFile(prefix: String, extension: String): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "${prefix}_${timeStamp}_"
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val frostDir = File(storageDir, prefix)
    if (!frostDir.exists()) frostDir.mkdirs()
    return File.createTempFile(imageFileName, extension, frostDir)
}

@Throws(IOException::class)
fun Context.createPrivateMediaFile(prefix: String, extension: String): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "${prefix}_${timeStamp}_"
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(imageFileName, extension, storageDir)
}

/**
 * Scan the path so that the media item is properly added to galleries
 *
 * See <a href="https://developer.android.com/training/camera/photobasics.html#TaskGallery">Docs</a>
 */
fun Context.scanMedia(f: File) {
    if (!f.exists()) return
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val contentUri = Uri.fromFile(f)
    mediaScanIntent.data = contentUri
    sendBroadcast(mediaScanIntent)
}