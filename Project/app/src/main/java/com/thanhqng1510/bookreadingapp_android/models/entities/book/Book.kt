package com.thanhqng1510.bookreadingapp_android.models.entities.book

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*

class Book(
    val title: String,
    val authors: Set<String>,
    val coverResId: Int?,
    val numPages: Int,
    val sharingSessionId: UUID?,
) {
    enum class STATUS {
        NEW, READING, FINISHED
    }

    var status: BookStatus = NewStatus(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        return title == other.title && authors != other.authors
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + authors.hashCode()
        return result
    }

    abstract class BookStatus(val master: Book, lastRead: LocalDate?, currentPage: Int) {
        open var lastRead: LocalDate? = lastRead
            set(value) {
                value?.run { field = value }
            }

        open var currentPage: Int = currentPage.coerceIn(1, master.numPages)
            @RequiresApi(Build.VERSION_CODES.O)
            set(value) {
                field = value.coerceIn(1, master.numPages)
                lastRead = LocalDate.now()
            }

        abstract val eVal: STATUS

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is BookStatus) return false

            if (lastRead != other.lastRead) return false
            if (currentPage != other.currentPage) return false
            if (eVal != other.eVal) return false

            return true
        }

        override fun hashCode(): Int {
            var result = lastRead?.hashCode() ?: 0
            result = 31 * result + currentPage
            result = 31 * result + eVal.hashCode()
            return result
        }
    }

    class NewStatus(master: Book) : BookStatus(master, null, 1) {
        override var lastRead: LocalDate?
            get() = super.lastRead
            set(value) {
                super.lastRead = value
                lastRead?.run { master.status = ReadingStatus(master, lastRead!!, currentPage) }
            }

        override var currentPage: Int
            get() = super.currentPage
            @RequiresApi(Build.VERSION_CODES.O)
            set(value) {
                super.currentPage = value
                if (currentPage > 1)
                    master.status = ReadingStatus(master, lastRead!!, currentPage)
            }

        override val eVal: STATUS
            get() = STATUS.NEW
    }

    class ReadingStatus(master: Book, lastRead: LocalDate, currentPage: Int) :
        BookStatus(master, lastRead, currentPage) {
        override var currentPage: Int
            get() = super.currentPage
            @RequiresApi(Build.VERSION_CODES.O)
            set(value) {
                super.currentPage = value
                if (currentPage == master.numPages)
                    master.status = FinishStatus(master, lastRead!!, currentPage)
            }

        override val eVal: STATUS
            get() = STATUS.READING
    }

    class FinishStatus(master: Book, lastRead: LocalDate, currentPage: Int) :
        BookStatus(master, lastRead, currentPage) {
        override var lastRead: LocalDate?
            get() = super.lastRead
            set(value) {
                super.lastRead = value
                lastRead?.run { master.status = ReadingStatus(master, lastRead!!, currentPage) }
            }

        override var currentPage: Int
            get() = super.currentPage
            @RequiresApi(Build.VERSION_CODES.O)
            set(value) {
                super.currentPage = value
                if (currentPage != master.numPages)
                    master.status = ReadingStatus(master, lastRead!!, currentPage)
            }

        override val eVal: STATUS
            get() = STATUS.FINISHED
    }
}