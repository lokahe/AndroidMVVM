package com.lokahe.androidmvvm.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.lokahe.androidmvvm.BuildConfig
import com.lokahe.androidmvvm.data.local.AppDatabase
import com.lokahe.androidmvvm.data.local.dao.PersonDao
import com.lokahe.androidmvvm.data.local.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import java.util.concurrent.Executors

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
        ).also { dbBuilder ->
            if (BuildConfig.DEBUG) {
                dbBuilder.setQueryCallback({ sql, args ->
                    Log.d("RoomLog", "$sql $args")
                }, Executors.newSingleThreadExecutor())
            }
        }.build()
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