// TODO: Add returning value for insertBook.
package com.thanhqng1510.bookreadingapp_android.datastore

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.thanhqng1510.bookreadingapp_android.datamodels.entities.Book
import com.thanhqng1510.bookreadingapp_android.datamodels.entities.BookWrapper
import com.thanhqng1510.bookreadingapp_android.datastore.localstore.LocalStore
import com.thanhqng1510.bookreadingapp_android.datastore.networkstore.NetworkStore
import com.thanhqng1510.bookreadingapp_android.datastore.sharedprefhelper.SharedPrefHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStore @Inject constructor(
    private val localStore: LocalStore,
    private val networkStore: NetworkStore,
    private val sharedPrefHelper: SharedPrefHelper
) {
    fun getAllBooks(): LiveData<List<Book>> = localStore.bookDao().getAll().map { it.map { b -> b.data } }

    fun insertBook(book: Book) {
        val bookWrapper = BookWrapper(book)
        localStore.bookDao().insert(bookWrapper)
    }
    // suspend fun getAllBooks(): List<Book> = localStore.getAllBooks()
}