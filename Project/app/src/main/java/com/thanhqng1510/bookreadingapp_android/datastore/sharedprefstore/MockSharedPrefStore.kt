package com.thanhqng1510.bookreadingapp_android.datastore.sharedprefstore

import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockSharedPref
class MockSharedPrefStore @Inject constructor(): ISharedPrefStore {
}