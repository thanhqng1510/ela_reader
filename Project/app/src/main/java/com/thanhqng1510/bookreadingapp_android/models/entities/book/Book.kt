package com.thanhqng1510.bookreadingapp_android.models.entities.book

import androidx.room.*
import com.thanhqng1510.bookreadingapp_android.models.entities.SharedConverters
import java.time.LocalDateTime
import java.util.*

@Entity(
    tableName = "books",
    indices = [Index(value = ["title"], unique = true)],
    // ignoredColumns = ["logUtil"] TODO: Find way to use LogUtil in Book
)
@TypeConverters(SharedConverters::class, BookConverter::class)
data class Book(
    val title: String,
    val authors: Set<String>,
    val coverResId: Int?,
    val numPages: Int,
    val dateAdded: LocalDateTime,
    val sharingSessionId: UUID?,
) {

    enum class STATUS {
        NEW, READING, FINISHED
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var id: Int = 0

    var lastRead: LocalDateTime? = null
        set(value) {
            value?.run {
                field = value
            } // ?: logUtil.warn("Attempt to set lastRead to null", true)
        }

    var currentPage: Int = 1
        set(value) {
            // if (value < 1 || value > numPages)
            // logUtil.warn("Attempt to set currentPage to out-of-bound value", true)
            field = value.coerceIn(1, numPages)
        }

    val status: STATUS
        get() {
            if (lastRead == null)
                return STATUS.NEW
            if (currentPage == numPages)
                return STATUS.FINISHED
            return STATUS.READING
        }
}
