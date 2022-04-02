package com.thanhqng1510.bookreadingapp_android.datastore

import com.thanhqng1510.bookreadingapp_android.datamodels.entities.Book
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
    fun getAllBooks(): List<Book> = localStore.bookDao().getAll().map { it.data }
}