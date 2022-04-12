package com.thanhqng1510.bookreadingapp_android.models.daos

import android.net.Uri
import androidx.room.*
import com.thanhqng1510.bookreadingapp_android.models.entities.SharedConverters
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import com.thanhqng1510.bookreadingapp_android.models.entities.book.BookConverter
import kotlinx.coroutines.flow.Flow

@Dao
@TypeConverters(SharedConverters::class, BookConverter::class)
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAll(): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book): Long

    @Delete
    suspend fun delete(book: Book): Int

    @Query("SELECT COUNT(*) FROM books WHERE uri = :uri")
    suspend fun countByUri(uri: Uri): Long

    @Query("SELECT COUNT(*) FROM books WHERE title GLOB :title")
    suspend fun countByLikedTitle(title: String): Long

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Book>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(vararg users: Book)
//
//    @MapInfo(keyColumn = "userName", valueColumn = "bookName")
//    @Query(
//        "SELECT user.name AS username, book.name AS bookname FROM user" +
//                "JOIN book ON user.id = book.user_id"
//    )
//    fun loadUserAndBookNames(): Map<String, List<String>>
}

//data class NameTuple(
//    @ColumnInfo(name = "first_name") val firstName: String?,
//    @ColumnInfo(name = "last_name") val lastName: String?
//)