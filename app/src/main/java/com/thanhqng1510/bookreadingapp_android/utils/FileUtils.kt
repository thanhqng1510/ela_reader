package com.thanhqng1510.bookreadingapp_android.utils

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object FileUtils {
    fun getFileDisplayName(filePath: String): String {
        return getFileName(filePath).replace("""\s+""".toRegex(), " ").split(" ")
            .joinToString(separator = " ") {
                it.replaceFirstChar { c ->
                    if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString()
                }
            }
    }

    fun getFileName(filePath: String): String = filePath.substringBeforeLast(".").trim()

    fun getFileExtension(filePath: String): String = filePath.substringAfterLast(".").trim()

    fun isUriOfContent(uri: Uri): Boolean = uri.scheme == contentSchemeUri

    fun isUriOfFile(uri: Uri): Boolean = uri.scheme == fileSchemeUri

    fun copyToAsync(
        inputStream: InputStream,
        outputStream: OutputStream,
        scope: CoroutineScope
    ) = scope.launch(Dispatchers.IO) {
        var read: Int
        val bufferSize = 1024 * 1024 // 1Mb
        val buffers = ByteArray(bufferSize)

        while (inputStream.read(buffers).also { read = it } != -1) {
            outputStream.write(buffers, 0, read)
        }
        inputStream.close()
        outputStream.close()
    }

    fun isExistingUri(uri: Uri): Boolean {
        return uri.path?.let {
            val file = File(it)
            return@let file.exists()
        } ?: false
    }

    fun isExistingFilePath(filePath: String): Boolean = File(filePath).exists()

    fun deleteAtPathAsync(filePath: String, scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        val file = File(filePath)
        if (file.exists())
            file.deleteRecursively()
    }

    fun getPdfPageCount(filePath: String): Int {
        val fileDescriptor = ParcelFileDescriptor.open(File(filePath), MODE_READ_ONLY)
        val renderer = PdfRenderer(fileDescriptor)

        return renderer.pageCount
    }

    fun getPdfThumbnail(filePath: String): Bitmap {
        val fileDescriptor = ParcelFileDescriptor.open(File(filePath), MODE_READ_ONLY)
        val renderer = PdfRenderer(fileDescriptor)
        val page = renderer.openPage(0)

        val thumbnailBitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(thumbnailBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        page.close()
        renderer.close()

        return thumbnailBitmap
    }

    private const val contentSchemeUri = "content"
    private const val fileSchemeUri = "file"
}