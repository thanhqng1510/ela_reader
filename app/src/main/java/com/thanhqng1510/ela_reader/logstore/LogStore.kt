package com.thanhqng1510.ela_reader.logstore

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thanhqng1510.ela_reader.models.daos.LogEntryDao
import com.thanhqng1510.ela_reader.models.entities.logentry.LogEntry
import com.thanhqng1510.ela_reader.models.entities.logentry.LogEntryConverter

@Database(entities = [LogEntry::class], exportSchema = false, version = 1)
@TypeConverters(LogEntryConverter::class)
abstract class LogStore : RoomDatabase() {
    abstract fun logEntryDao(): LogEntryDao
}