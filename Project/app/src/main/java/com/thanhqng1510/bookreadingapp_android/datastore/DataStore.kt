package com.thanhqng1510.bookreadingapp_android.datastore

import com.thanhqng1510.bookreadingapp_android.datastore.localstore.LocalStore
import com.thanhqng1510.bookreadingapp_android.datastore.networkstore.NetworkStore
import com.thanhqng1510.bookreadingapp_android.datastore.sharedprefhelper.SharedPrefHelper
import com.thanhqng1510.bookreadingapp_android.logstore.LogUtil
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStore @Inject constructor(
    private val localStore: LocalStore,
    private val networkStore: NetworkStore,
    private val sharedPrefHelper: SharedPrefHelper,
    private val logUtil: LogUtil
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    fun getAllBooks(): Flow<List<Book>> = localStore.bookDao().getAll()

    fun insertBookAsync(book: Book) = scope.launch {
        val bookId = localStore.bookDao().insert(book)
        logUtil.info("Added book with ID: $bookId")
    }
}