package com.thanhqng1510.bookreadingapp_android.datastore

import androidx.lifecycle.LiveData
import com.thanhqng1510.bookreadingapp_android.datastore.localstore.LocalStore
import com.thanhqng1510.bookreadingapp_android.datastore.networkstore.NetworkStore
import com.thanhqng1510.bookreadingapp_android.datastore.sharedprefhelper.SharedPrefHelper
import com.thanhqng1510.bookreadingapp_android.models.entities.book.Book
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStore @Inject constructor(
    private val localStore: LocalStore,
    private val networkStore: NetworkStore,
    private val sharedPrefHelper: SharedPrefHelper
) {
    fun getAllBooks(): LiveData<List<Book>> = localStore.bookDao().getAll()

    // TODO: Add returning value for insertBook
    fun insertBook(book: Book) = localStore.bookDao().insert(book)
}