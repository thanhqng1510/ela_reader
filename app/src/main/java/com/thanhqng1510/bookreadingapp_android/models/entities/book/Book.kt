package com.thanhqng1510.bookreadingapp_android.models.entities.book

import android.net.Uri
import androidx.room.*
import com.thanhqng1510.bookreadingapp_android.models.entities.SharedConverters
import com.thanhqng1510.bookreadingapp_android.utils.FileUtils
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity(
    tableName = "books",
    indices = [Index(value = ["fileUri"], unique = true),
        Index(value = ["thumbnailUri"], unique = true)],
)
@TypeConverters(SharedConverters::class, BookConverter::class)
data class Book(
    val title: String,
    val authors: Set<String>,
    val thumbnailUri: Uri,
    val numPages: Int,
    val dateAdded: LocalDateTime,
    val fileType: String,
    var fileUri: Uri,
    var sharingSessionId: UUID?
) : Serializable {
    enum class STATUS {
        NEW, READING, FINISHED, ERROR
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var id: Long = 0L

    var lastRead: LocalDateTime? = null // TODO: Update this
        set(value) {
            value?.run {
                field = value
            }
        }

    var currentPage: Int = 1 // TODO: Update this
        set(value) {
            field = value.coerceIn(1, numPages)
        }

    val status: STATUS
        get() {
            if (!FileUtils.isExistingUri(fileUri))
                return STATUS.ERROR
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
        if (thumbnailUri != other.thumbnailUri) return false
        if (numPages != other.numPages) return false
        if (dateAdded != other.dateAdded) return false
        if (fileType != other.fileType) return false
        if (fileUri != other.fileUri) return false
        if (sharingSessionId != other.sharingSessionId) return false
        if (id != other.id) return false
        if (lastRead != other.lastRead) return false
        if (currentPage != other.currentPage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + authors.hashCode()
        result = 31 * result + thumbnailUri.hashCode()
        result = 31 * result + numPages.hashCode()
        result = 31 * result + dateAdded.hashCode()
        result = 31 * result + fileType.hashCode()
        result = 31 * result + fileUri.hashCode()
        result = 31 * result + sharingSessionId.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + lastRead.hashCode()
        result = 31 * result + currentPage.hashCode()
        return result
    }
}