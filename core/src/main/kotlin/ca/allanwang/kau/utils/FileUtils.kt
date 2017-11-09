package ca.allanwang.kau.utils

import java.io.File
import java.io.InputStream

/**
 * Created by Allan Wang on 2017-08-04.
 */
fun File.copyFromInputStream(inputStream: InputStream)
        = inputStream.use { input -> outputStream().use { output -> input.copyTo(output) } }