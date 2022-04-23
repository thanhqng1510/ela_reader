package com.thanhqng1510.bookreadingapp_android.datastore.localstore

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thanhqng1510.bookreadingapp_android.models.daos.BookDao
import com.thanhqng1510.bookreadingapp_android.models.daos.BookmarkDao
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.models.entities.bookmark.Bookmark

@Database(entities = [Book::class, Bookmark::class], exportSchema = false, version = 1)
abstract class LocalStore : RoomDatabase() {
    abstract fun bookDao(): BookDao

    abstract fun bookmarkDao(): BookmarkDao
}