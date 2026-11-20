package com.lokahe.database.di

import android.content.Context
import androidx.room.Room
import com.lokahe.database.BuildConfig
import com.lokahe.database.AppDatabase
import com.lokahe.database.dao.PersonDao
import com.lokahe.log.L
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
        )
            // Local DB only holds demo Person rows; dropping the legacy posts
            // table on upgrade is fine, so no Migration is authored.
            .fallbackToDestructiveMigrationOnDowngrade()
            .fallbackToDestructiveMigration()
            .also { dbBuilder ->
                if (BuildConfig.DEBUG) {
                    dbBuilder.setQueryCallback({ sql, args ->
                        L.d("RoomLog") { "$sql $args" }
                    }, Executors.newSingleThreadExecutor())
                }
            }.build()
    }

    @Provides
    @Singleton
    fun providePersonDao(database: AppDatabase): PersonDao {
        return database.personDao()
    }
}