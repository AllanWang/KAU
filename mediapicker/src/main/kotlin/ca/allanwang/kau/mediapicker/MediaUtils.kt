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
import java.util.ArrayList
import java.util.Date
import java.util.Locale

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

/**
 * Creates a folder named [prefix] as well as a new file with the prefix, current time, and extension.
 */
@Throws(IOException::class)
fun createMediaFile(prefix: String, extension: String): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "${prefix}_${timeStamp}_"
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val prefixDir = File(storageDir, prefix)
    if (!prefixDir.exists()) prefixDir.mkdirs()
    return File.createTempFile(imageFileName, extension, prefixDir)
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
