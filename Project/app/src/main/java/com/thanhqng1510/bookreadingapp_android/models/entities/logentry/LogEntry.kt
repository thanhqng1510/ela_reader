package com.thanhqng1510.bookreadingapp_android.models.entities.logentry

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import java.time.LocalDate

@Entity(tableName = "logentries")
data class LogEntry(
    @TypeConverters(LogEntryConverter::class)
    @NonNull
    val level: LogUtil.LOGLEVEL,

    @TypeConverters(LogEntryConverter::class)
    @NonNull
    val timeStamp: LocalDate,

    @NonNull
    val threadId: Long,

    @NonNull
    val threadName: String,

    @NonNull
    val message: String,

    val stackTraceStr: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LogEntry) return false

        if (level != other.level) return false
        if (timeStamp != other.timeStamp) return false
        if (threadId != other.threadId) return false
        if (threadName != other.threadName) return false
        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        var result = level.hashCode()
        result = 31 * result + timeStamp.hashCode()
        result = 31 * result + threadId.hashCode()
        result = 31 * result + threadName.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }
}