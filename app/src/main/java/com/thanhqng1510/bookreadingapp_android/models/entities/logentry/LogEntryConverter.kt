package com.thanhqng1510.bookreadingapp_android.models.entities.logentry

import androidx.room.TypeConverter
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil

class LogEntryConverter {
    @TypeConverter
    fun stringToLogLevel(string: String) =
        LogUtil.LOGLEVEL.forTag(string) ?: throw IllegalArgumentException()

    @TypeConverter
    fun logLevelToString(loglevel: LogUtil.LOGLEVEL) = loglevel.tag
}