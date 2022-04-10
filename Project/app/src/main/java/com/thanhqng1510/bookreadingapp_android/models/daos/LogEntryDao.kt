package com.thanhqng1510.bookreadingapp_android.models.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.thanhqng1510.bookreadingapp_android.models.entities.logentry.LogEntry

@Dao
interface LogEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(logEntry: LogEntry): Long
}