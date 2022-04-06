package com.thanhqng1510.bookreadingapp_android.datamodels.entities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.*

class BookWrapperConverter {
    @TypeConverter
    fun stringToBookHelper(string: String): Book {
        val tokens = string.split("-bookToStringHelper-")

        val title = tokens[0]
        val authors = stringToAuthorsHelper(tokens[1])
        val coverResId = tokens[2].let { if (it.isEmpty()) null else it.toInt() }
        val numPages = tokens[3].toInt()
        val sharingSessionId = tokens[4].let { if (it.isEmpty()) null else UUID.fromString(it) }

        val book = Book(title, authors, coverResId, numPages, sharingSessionId)
        val bookStatus = stringToBookStatusHelper(book, tokens[5])
        book.status = bookStatus
        return book
    }

    @TypeConverter
    fun bookToStringHelper(book: Book): String {
        return listOf(book.title,
                      authorsToStringHelper(book.authors),
                      book.coverResId?.toString() ?: "",
                      book.numPages.toString(),
                      book.sharingSessionId?.toString() ?: "",
                      bookStatusToStringHelper(book.status)).joinToString(separator = "-bookToStringHelper-")
    }

    private fun stringToAuthorsHelper(string: String): Set<String> = string.split("-authorsToStringHelper-").toSet()

    private fun authorsToStringHelper(authors: Set<String>): String = authors.joinToString(separator = "-authorsToStringHelper-")

    private fun stringToBookStatusHelper(master: Book, string: String): Book.BookStatus {
        val tokens = string.split("-bookStatusToStringHelper-")
        val lastRead: LocalDate? = tokens[0].let { if (it.isEmpty()) null else LocalDate.parse(it) }
        val currentPage = tokens[1].toInt()

        return when(Book.STATUS.valueOf(tokens[2])) {
            Book.STATUS.NEW -> Book.NewStatus(master)
            Book.STATUS.READING -> Book.ReadingStatus(master, lastRead!!, currentPage)
            Book.STATUS.FINISHED -> Book.FinishStatus(master, lastRead!!, currentPage)
        }
    }

    private fun bookStatusToStringHelper(bookStatus: Book.BookStatus): String {
        return listOf(bookStatus.lastRead?.toString() ?: "",
                      bookStatus.currentPage.toString(),
                      bookStatus.eVal.name).joinToString(separator = "-bookStatusToStringHelper-")
    }
}