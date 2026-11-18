package com.lokahe.store.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** App-wide generic settings store ("settings"). */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Session/profile store ("user_prefs"). Consumed by the app-side [com.lokahe.androidmvvm.data.local.UserManager]
 * facade and by the :auth module's TokenStore / PkceStore implementations.
 * The Hilt binding lives in [com.lokahe.store.di.PreferencesModule].
 */
val Context.userStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")