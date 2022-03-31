package com.thanhqng1510.bookreadingapp_android.datastore.localstore

import com.thanhqng1510.bookreadingapp_android.datamodels.Book
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Deferred
import javax.inject.Singleton

interface ILocalStore {
    fun getBookListAsync(): Deferred<MutableList<Book>>
}

@Module
@InstallIn(SingletonComponent::class)
object LocalStoreModule {
    @Singleton
    @Provides
    @MockLocal
    fun provideMock(): ILocalStore = MockLocalStore()
}