package com.thanhqng1510.bookreadingapp_android.datastore.networkstore

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

interface INetworkStore {
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkStoreModule {
    @Singleton
    @Provides
    @MockNetwork
    fun provideMock(): INetworkStore = MockNetworkStore()
}