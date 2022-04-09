package com.thanhqng1510.bookreadingapp_android.models.entities.logentry

import androidx.room.TypeConverter
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil

class LogEntryConverter {
    @TypeConverter
    fun stringToLogLevel(string: String): LogUtil.LOGLEVEL = LogUtil.LOGLEVEL.forTag(string)

    @TypeConverter
    fun logLevelToString(loglevel: LogUtil.LOGLEVEL): String = loglevel.tag
}