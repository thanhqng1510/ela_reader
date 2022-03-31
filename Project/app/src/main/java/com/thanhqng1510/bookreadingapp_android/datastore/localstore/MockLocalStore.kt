package com.thanhqng1510.bookreadingapp_android.datastore.localstore

import com.thanhqng1510.bookreadingapp_android.models.Book
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockLocal
class MockLocalStore @Inject constructor() : ILocalStore {
    override fun getBookListAsync(): Deferred<MutableList<Book>> = CoroutineScope(Dispatchers.IO).async {
        delay(5000L)

        return@async mutableListOf<Book>().apply {
            repeat(15) {
                add(Book(null, "<title>", setOf("<author1>", "<author2>"), 100, null, null, null))
            }
        }
    }
}