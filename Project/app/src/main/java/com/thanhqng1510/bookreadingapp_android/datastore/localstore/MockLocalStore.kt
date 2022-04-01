package com.thanhqng1510.bookreadingapp_android.datastore.localstore

import com.thanhqng1510.bookreadingapp_android.datamodels.entities.Book
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockLocalStore @Inject constructor() {
    suspend fun getAllBooks(): List<Book> {
        delay(2000L)
        return mutableListOf<Book>().apply {
            repeat(15) {
                add(Book("<title_${it}>", setOf("<${it}_author1>", "<${it}_author2>"), null, 100, null))
            }
        }
    }
}