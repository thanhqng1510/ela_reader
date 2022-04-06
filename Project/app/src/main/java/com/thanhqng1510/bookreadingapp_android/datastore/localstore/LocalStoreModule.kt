package com.thanhqng1510.bookreadingapp_android.datastore.localstore

import android.content.Context
import androidx.room.Room
import com.thanhqng1510.bookreadingapp_android.datamodels.daos.BookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class)
object LocalStoreModule {
    @Provides @Singleton
    fun provideRoom(@ApplicationContext context: Context): LocalStore = Room.databaseBuilder(
        context, LocalStore::class.java, "local-db"
    ).build()

    @Provides @Singleton
    fun provideBookDao(database: LocalStore): BookDao = database.bookDao()
}