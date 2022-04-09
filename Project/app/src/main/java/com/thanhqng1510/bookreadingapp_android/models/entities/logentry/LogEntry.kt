package com.thanhqng1510.bookreadingapp_android.models.entities.logentry

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.SharedConverters
import java.time.LocalDateTime

@Entity(tableName = "logentries")
@TypeConverters(SharedConverters::class, LogEntryConverter::class)
data class LogEntry(
    val level: LogUtil.LOGLEVEL,
    val timeStamp: LocalDateTime,
    val threadId: Long,
    val threadName: String,
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