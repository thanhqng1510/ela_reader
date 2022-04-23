package com.thanhqng1510.bookreadingapp_android.models.entities.bookmark

import androidx.room.*
import com.thanhqng1510.bookreadingapp_android.models.entities.SharedConverters
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import java.time.LocalDateTime

@Entity(
    tableName = "bookmarks",
    indices = [Index(value = ["bookId", "page"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = arrayOf("rowid"),
        childColumns = arrayOf("bookId"),
        onDelete = ForeignKey.CASCADE
    )]
)
@TypeConverters(SharedConverters::class)
data class Bookmark(
    val page: Int,
    val bookId: Long,
    val dateAdded: LocalDateTime,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var id: Long = 0L
}