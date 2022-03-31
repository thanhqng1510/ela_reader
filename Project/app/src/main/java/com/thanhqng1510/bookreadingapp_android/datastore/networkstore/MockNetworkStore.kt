package com.thanhqng1510.bookreadingapp_android.datastore.networkstore

import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockNetwork
class MockNetworkStore @Inject constructor() : INetworkStore {
}