package com.thanhqng1510.bookreadingapp_android.models.daos

import androidx.room.*
import com.thanhqng1510.bookreadingapp_android.models.entities.bookmark.Bookmark
import com.thanhqng1510.bookreadingapp_android.models.entities.bookmark.BookmarkWithBook
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks")
    fun getAllAsFlow(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE rowid = :id")
    suspend fun getById(id: Long): Bookmark?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark): Long

    @Delete
    suspend fun delete(bookmark: Bookmark): Int

    @Update
    suspend fun update(bookmark: Bookmark): Int

    @Transaction
    @Query("SELECT * FROM bookmarks")
    fun getAllWithBookAsFlow(): Flow<List<BookmarkWithBook>>
}