package com.thanhqng1510.bookreadingapp_android.mocks

import androidx.core.content.ContextCompat
import com.thanhqng1510.bookreadingapp_android.models.Book

object MockBooks {
    private val books = mutableListOf<Book>()

    fun getBooks(): List<Book> {
        if (books.isEmpty()) {
            for (i in 0..14)
                books.add(Book(null, "<title>", arrayOf("<author1>", "<author2>"), 100, null))
        }
        return books
    }
}