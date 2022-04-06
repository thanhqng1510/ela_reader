package com.thanhqng1510.bookreadingapp_android.models.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thanhqng1510.bookreadingapp_android.models.entities.book.BookWrapper

@Dao
interface BookDao {
    @Query("SELECT rowid, * FROM books")
    fun getAll(): LiveData<List<BookWrapper>>

    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insert(bookWrapper: BookWrapper)

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