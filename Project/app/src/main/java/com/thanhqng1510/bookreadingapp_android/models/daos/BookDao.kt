package com.thanhqng1510.bookreadingapp_android.models.daos

import androidx.room.*
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    /*
    * SQLite considers NULL values to be smaller than any other values for sorting purposes.
    * Hence, NULLs naturally appear at the beginning of an ASC order-by and at the end of a DESC order-by.
    * This can be changed using the "ASC NULLS LAST" or "DESC NULLS FIRST" syntax.
    * */
    @Query("SELECT * FROM books")
    fun getAll(): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book): Long

    @Delete
    fun delete(bookWrapper: BookWrapper)

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Book>
//
//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Book
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(vararg users: Book)
//
//    @Delete
//    fun delete(user: Book)
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