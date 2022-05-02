package com.thanhqng1510.ela_reader.utils.file_utils

/**
 * Represents a file system path of a valid file
 */
open class FilePath(rawPath: String) : Path(rawPath) {
    fun getFileName() = FileName(rawPath.substringAfterLast("/").substringBeforeLast(".").trim())

    fun getFileExtension() = rawPath.substringAfterLast(".").trim()
}