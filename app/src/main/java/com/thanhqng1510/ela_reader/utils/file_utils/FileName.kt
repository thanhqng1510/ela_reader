package com.thanhqng1510.ela_reader.utils.file_utils

import java.util.*

/**
 * Represents a file name
 *
 * @param rawName Full name without extension of a file extracted from file path
 */
class FileName(val rawName: String) {
    /**
     * Get the prettified file name used for display purpose
     */
    fun getDisplayName() = rawName.replace("""\s+""".toRegex(), " ").split(" ")
        .joinToString(separator = " ") {
            it.replaceFirstChar { c ->
                if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString()
            }

        }
}