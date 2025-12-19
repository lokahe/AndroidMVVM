package com.lokahe.androidmvvm.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lokahe.androidmvvm.models.UserPreferences
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

@Singleton
class UserPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AGE = intPreferencesKey("user_age")
        val USER_GENDER = stringPreferencesKey("user_gender")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    fun observeUserPreferences(): Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            UserPreferences(
                userName = it[PreferencesKeys.USER_NAME] ?: "",
                userAge = it[PreferencesKeys.USER_AGE] ?: 0,
                userGender = it[PreferencesKeys.USER_GENDER] ?: "",
                isLoggedIn = it[PreferencesKeys.IS_LOGGED_IN] ?: false
            )
        }

    // Write data
    suspend fun updateUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun updateUserAge(age: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_AGE] = age
        }
    }

    suspend fun updateLoginStatus(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun updateGender(gender: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_GENDER] = gender
        }
    }

    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}