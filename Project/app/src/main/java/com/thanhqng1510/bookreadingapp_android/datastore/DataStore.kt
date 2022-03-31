package com.thanhqng1510.bookreadingapp_android.datastore

import com.thanhqng1510.bookreadingapp_android.datastore.localstore.ILocalStore
import com.thanhqng1510.bookreadingapp_android.datastore.localstore.MockLocal
import com.thanhqng1510.bookreadingapp_android.datastore.networkstore.INetworkStore
import com.thanhqng1510.bookreadingapp_android.datastore.networkstore.MockNetwork
import com.thanhqng1510.bookreadingapp_android.datastore.sharedprefstore.ISharedPrefStore
import com.thanhqng1510.bookreadingapp_android.datastore.sharedprefstore.MockSharedPref
import com.thanhqng1510.bookreadingapp_android.models.Book
import kotlinx.coroutines.Deferred
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStore @Inject constructor(
    @MockLocal private val localStore: ILocalStore,
    @MockNetwork val networkStore: INetworkStore,
    @MockSharedPref private val sharedPrefStore: ISharedPrefStore
) {
    fun getBookListAsync(): Deferred<MutableList<Book>> = localStore.getBookListAsync()
}