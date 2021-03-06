package com.thanhqng1510.ela_reader.models.daos

import android.net.Uri
import androidx.room.*
import com.thanhqng1510.ela_reader.models.entities.SharedConverters
import com.thanhqng1510.ela_reader.models.entities.book.Book
import com.thanhqng1510.ela_reader.models.entities.book.BookConverter
import com.thanhqng1510.ela_reader.models.entities.book.BookWithBookmarks
import kotlinx.coroutines.flow.Flow

@Dao
@TypeConverters(SharedConverters::class, BookConverter::class)
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllAsFlow(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE rowid = :id")
    suspend fun getById(id: Long): Book?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book): Long

    @Delete
    suspend fun delete(book: Book): Int

    @Update
    suspend fun update(book: Book): Int

    @Query("SELECT COUNT(*) FROM books WHERE fileUri = :fileUri")
    suspend fun countByFileUri(fileUri: Uri): Long

    @Transaction
    @Query("SELECT * FROM books")
    fun getAllWithBookmarksAsFlow(): Flow<List<BookWithBookmarks>>
}