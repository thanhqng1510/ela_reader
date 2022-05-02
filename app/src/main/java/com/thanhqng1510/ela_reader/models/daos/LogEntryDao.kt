package com.thanhqng1510.ela_reader.models.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.thanhqng1510.ela_reader.models.entities.logentry.LogEntry

@Dao
interface LogEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(logEntry: LogEntry): Long
}