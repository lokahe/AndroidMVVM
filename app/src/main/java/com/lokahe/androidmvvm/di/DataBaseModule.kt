package com.lokahe.androidmvvm.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lokahe.androidmvvm.dao.PersonDao
import com.lokahe.androidmvvm.dao.PostDao
import com.lokahe.androidmvvm.models.Person
import com.lokahe.androidmvvm.models.Post
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

// Add your entities to the array, e.g. entities = [User::class]
@Database(entities = [Person::class, Post::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // abstract fun userDao(): UserDao
    abstract fun personDao(): PersonDao
    abstract fun postDao(): PostDao
}

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
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