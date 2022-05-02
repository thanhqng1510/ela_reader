package com.thanhqng1510.ela_reader.utils.file_utils

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object FileUtils {
    fun Uri.isContentScheme() = scheme == "content"

    fun Uri.isFileScheme() = scheme == "file"

    fun Uri.isExist() = path?.let {
        val file = File(it)
        return@let file.exists()
    } ?: false

    fun InputStream.copyToAsync(
        outputStream: OutputStream,
        scope: CoroutineScope
    ) = scope.launch(Dispatchers.IO) {
        var read: Int
        val bufferSize = 1024 * 1024 // 1Mb
        val buffers = ByteArray(bufferSize)

        while (read(buffers).also { read = it } != -1) {
            outputStream.write(buffers, 0, read)
        }
        close()
        outputStream.close()
    }
}