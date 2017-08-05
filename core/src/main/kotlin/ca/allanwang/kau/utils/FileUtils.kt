package ca.allanwang.kau.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Allan Wang on 2017-08-04.
 */
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

fun File.copyFromInputStream(inputStream: InputStream)
        = inputStream.use { input -> outputStream().use { output -> input.copyTo(output) } }