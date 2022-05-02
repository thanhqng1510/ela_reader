package com.thanhqng1510.ela_reader.models.entities.logentry

import androidx.room.TypeConverter
import com.thanhqng1510.ela_reader.logstore.LogUtil

class LogEntryConverter {
    @TypeConverter
    fun stringToLogLevel(string: String) =
        LogUtil.LogLevel.forTag(string) ?: throw IllegalArgumentException()

    @TypeConverter
    fun logLevelToString(logLevel: LogUtil.LogLevel) = logLevel.tag
}