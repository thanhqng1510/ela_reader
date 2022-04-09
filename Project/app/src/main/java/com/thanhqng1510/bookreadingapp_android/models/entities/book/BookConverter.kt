package com.thanhqng1510.bookreadingapp_android.models.entities.book

import androidx.room.TypeConverter
import java.util.*

class BookConverter {
    @TypeConverter
    fun setToStringHelper(authors: Set<String>): String =
        authors.joinToString(separator = "-authorsToStringHelper-")

    @TypeConverter
    fun stringToSetHelper(string: String): Set<String> =
        string.split("-authorsToStringHelper-").toSet()

    @TypeConverter
    fun uuidToStringHelper(sharingSessionId: UUID?): String =
        sharingSessionId?.toString() ?: ""

    @TypeConverter
    fun stringToUuidHelper(string: String): UUID? =
        if (string.isEmpty()) null else UUID.fromString(string)
}