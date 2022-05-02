package com.thanhqng1510.ela_reader.utils.file_utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * Represents a file system path
 *
 * @param rawPath A string contains a valid full path of resource in memory
 */
open class Path(val rawPath: String) {
    fun isExist() = File(rawPath).exists()

    fun deleteAsync(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        val file = File(rawPath)
        if (file.exists())
            file.deleteRecursively()
    }
}