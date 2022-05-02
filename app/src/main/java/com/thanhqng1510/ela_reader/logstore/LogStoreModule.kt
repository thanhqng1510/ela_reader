package com.thanhqng1510.ela_reader.logstore

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
object LogStoreModule {
    @Provides
    @Singleton
    fun provideRoom(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, LogStore::class.java, "local-logdb"
    ).build()

    @Provides
    @Singleton
    fun provideLogEntryDao(database: LogStore) = database.logEntryDao()
}