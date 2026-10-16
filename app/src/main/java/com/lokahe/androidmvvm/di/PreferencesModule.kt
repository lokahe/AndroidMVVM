package com.lokahe.androidmvvm.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.lokahe.androidmvvm.data.local.dataStore
import com.lokahe.androidmvvm.data.local.userStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    /**
     * Session/profile store ("user_prefs"). Consumed by UserManager and by
     * the :auth module's TokenStore / PkceStore implementations.
     */
    @Provides
    @Singleton
    @Named("user_prefs")
    fun provideUserStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userStore
    }
}