package com.lokahe.androidmvvm.di

import android.content.Context
import androidx.room.Room
import com.lokahe.androidmvvm.data.local.dao.PersonDao
import com.lokahe.androidmvvm.data.local.dao.PostDao
import com.lokahe.androidmvvm.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db" // The name of your SQLite file
        ).build()
    }

    @Provides
    @Singleton
    fun providePersonDao(database: AppDatabase): PersonDao {
        return database.personDao()
    }

    @Provides
    @Singleton
    fun providePostDao(database: AppDatabase): PostDao {
        return database.postDao()
    }

}