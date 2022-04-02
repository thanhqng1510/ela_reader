package com.thanhqng1510.bookreadingapp_android.datastore.localstore

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thanhqng1510.bookreadingapp_android.datamodels.daos.BookDao
import com.thanhqng1510.bookreadingapp_android.datamodels.entities.BookWrapper
import com.thanhqng1510.bookreadingapp_android.datamodels.entities.BookWrapperConverter

@Database(entities = [BookWrapper::class], exportSchema = false, version = 1)
@TypeConverters(BookWrapperConverter::class)
abstract class LocalStore : RoomDatabase() {
    abstract fun bookDao(): BookDao
}