package com.thanhqng1510.ela_reader.datastore.localstore

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStoreModule {
    @Provides
    @Singleton
    fun provideRoom(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, LocalStore::class.java, "local-datadb"
    ).build()

    @Provides
    @Singleton
    fun provideBookDao(database: LocalStore) = database.bookDao()

    @Provides
    @Singleton
    fun provideBookmarkDao(database: LocalStore) = database.bookmarkDao()
}