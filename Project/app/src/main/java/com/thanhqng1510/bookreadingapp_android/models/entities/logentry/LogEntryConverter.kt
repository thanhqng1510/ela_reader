package com.thanhqng1510.bookreadingapp_android.models.entities.logentry

import androidx.room.TypeConverter
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import java.time.LocalDate

class LogEntryConverter {
    @TypeConverter
    fun stringToLogLevel(string: String): LogUtil.LOGLEVEL = LogUtil.LOGLEVEL.forTag(string)

    @TypeConverter
    fun logLevelToString(loglevel: LogUtil.LOGLEVEL): String = loglevel.tag

    @TypeConverter
    fun stringToTimeStamp(string: String): LocalDate = LocalDate.parse(string)

    @TypeConverter
    fun timeStampToString(timeStamp: LocalDate): String = timeStamp.toString()
}