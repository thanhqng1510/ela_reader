package com.thanhqng1510.bookreadingapp_android.models.entities.book

import android.net.Uri
import androidx.room.TypeConverter
import java.util.*

class BookConverter {
    @TypeConverter
    fun setToString(authors: Set<String>): String =
        authors.joinToString(separator = "-authorsToStringHelper-")

    @TypeConverter
    fun stringToSet(string: String): Set<String> =
        string.split("-authorsToStringHelper-").toSet()

    @TypeConverter
    fun nullableUuidToString(sharingSessionId: UUID?): String =
        sharingSessionId?.toString() ?: ""

    @TypeConverter
    fun stringToNullableUuid(string: String): UUID? =
        if (string.isEmpty()) null else UUID.fromString(string)

    @TypeConverter
    fun uriToString(uri: Uri): String = uri.toString()

    @TypeConverter
    fun stringToUri(string: String): Uri = Uri.parse(string)
}