package com.thanhqng1510.bookreadingapp_android.datamodels.daos

import androidx.room.Dao
import androidx.room.Query
import com.thanhqng1510.bookreadingapp_android.datamodels.entities.BookWrapper

@Dao
interface BookDao {
    @Query("SELECT rowid, * FROM books")
    fun getAll(): List<BookWrapper>

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