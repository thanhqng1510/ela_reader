package com.thanhqng1510.bookreadingapp_android.datastore.localstore

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thanhqng1510.bookreadingapp_android.models.daos.BookDao
import com.thanhqng1510.bookreadingapp_android.models.entities.book.BookWrapper
import com.thanhqng1510.bookreadingapp_android.models.entities.book.BookWrapperConverter

@Database(entities = [BookWrapper::class], exportSchema = false, version = 1)
@TypeConverters(BookWrapperConverter::class)
abstract class LocalStore : RoomDatabase() {
    abstract fun bookDao(): BookDao
}