package com.thanhqng1510.bookreadingapp_android.datastore.sharedprefstore

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

interface ISharedPrefStore {
}

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefStoreModule {
    @Singleton
    @Provides
    @MockSharedPref
    fun provideMock(): ISharedPrefStore = MockSharedPrefStore()
}