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
fun File.copyFromInputStream(inputStream: InputStream)
        = inputStream.use { input -> outputStream().use { output -> input.copyTo(output) } }