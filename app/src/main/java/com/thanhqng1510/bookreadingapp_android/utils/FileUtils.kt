package com.thanhqng1510.bookreadingapp_android.utils

import android.net.Uri
import com.thanhqng1510.bookreadingapp_android.application.MainApplication
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

    fun isUriOfContent(uri: Uri): Boolean = uri.scheme == MainApplication.contentSchemeUri

    fun isUriOfFile(uri: Uri): Boolean = uri.scheme == MainApplication.fileSchemeUri
}