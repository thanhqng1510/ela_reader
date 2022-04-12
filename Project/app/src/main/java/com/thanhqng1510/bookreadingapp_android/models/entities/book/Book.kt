package com.thanhqng1510.bookreadingapp_android.models.entities.book

import android.net.Uri
import androidx.room.*
import com.thanhqng1510.bookreadingapp_android.models.entities.SharedConverters
import java.time.LocalDateTime
import java.util.*

@Entity(
    tableName = "books",
    indices = [Index(value = ["title"], unique = true), Index(value = ["uri"], unique = true)],
)
@TypeConverters(SharedConverters::class, BookConverter::class)
data class Book(
    val title: String,
    val authors: Set<String>,
    val coverResId: Int?,
    val numPages: Int, // TODO: Can this be modified ?
    val dateAdded: LocalDateTime,
    val fileType: String,
    var uri: Uri,
    var sharingSessionId: UUID?,
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
            }
        }

    var currentPage: Int = 1
        set(value) {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Book) return false

        if (title != other.title) return false
        if (authors != other.authors) return false
        if (coverResId != other.coverResId) return false
        if (numPages != other.numPages) return false
        if (dateAdded != other.dateAdded) return false
        if (fileType != other.fileType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + authors.hashCode()
        result = 31 * result + coverResId.hashCode()
        result = 31 * result + numPages.hashCode()
        result = 31 * result + dateAdded.hashCode()
        result = 31 * result + fileType.hashCode()
        return result
    }
}
