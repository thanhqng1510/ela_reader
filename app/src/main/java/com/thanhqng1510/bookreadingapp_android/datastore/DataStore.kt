package com.thanhqng1510.bookreadingapp_android.datastore

import android.net.Uri
import com.thanhqng1510.bookreadingapp_android.datastore.localstore.LocalStore
import com.thanhqng1510.bookreadingapp_android.datastore.networkstore.NetworkStore
import com.thanhqng1510.bookreadingapp_android.datastore.sharedprefhelper.SharedPrefHelper
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStore @Inject constructor(
    private val localStore: LocalStore,
    private val networkStore: NetworkStore,
    private val sharedPrefHelper: SharedPrefHelper,
    private val logUtil: LogUtil
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun getAllBooks(): Flow<List<Book>> = localStore.bookDao().getAll()

    fun getBookByIdAsync(id: Long): Deferred<Book?> = scope.async {
        return@async localStore.bookDao().getById(id)
    }

    fun insertBook(book: Book) = scope.launch {
        val bookId = localStore.bookDao().insert(book)
        logUtil.info("Added book with ID: $bookId")
    }

    fun deleteBook(book: Book) = scope.launch {
        val bookDeleted = localStore.bookDao().delete(book)
        logUtil.info("Deleted $bookDeleted book(s) with ID: ${book.id}")
    }

    fun countBookByFileUriAsync(fileUri: Uri): Deferred<Long> = scope.async {
        return@async localStore.bookDao().countByFileUri(fileUri)
    }
}