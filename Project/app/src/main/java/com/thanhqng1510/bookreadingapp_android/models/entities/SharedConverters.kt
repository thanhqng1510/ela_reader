package com.thanhqng1510.bookreadingapp_android.models.entities

import androidx.room.TypeConverter
import java.time.LocalDateTime

class SharedConverters {
    @TypeConverter
    fun stringToLocalDateTime(string: String): LocalDateTime? =
        if (string.isEmpty()) null else LocalDateTime.parse(string)

    @TypeConverter
    fun localDateTimeToString(timeStamp: LocalDateTime?): String = timeStamp?.toString() ?: ""
}