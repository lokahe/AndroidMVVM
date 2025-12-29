package com.lokahe.androidmvvm.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.lokahe.androidmvvm.models.network.LoginResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

val Context.userStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    companion object {
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    }

    /**
     * Helper to get just the token
     */
    val userTokenFlow: Flow<String?> = context.userStore.data.map { prefs ->
        prefs[USER_TOKEN_KEY]
    }

    /**
     * Saves the full LoginResponse object and the token separately for easy access
     */
    suspend fun saveUser(response: LoginResponse) {
        context.userStore.edit { prefs ->
            // 1. Save the token specifically (often needed for Interceptors)
            response.userToken?.let { token ->
                prefs[USER_TOKEN_KEY] = token
            }

            // 2. Save the entire object as a JSON string
            val json = gson.toJson(response)
            prefs[USER_DATA_KEY] = json
        }
    }

    /**
     * Retrieves the full User object as a Flow
     */
    val userFlow: Flow<LoginResponse?> = context.userStore.data.map { prefs ->
        val json = prefs[USER_DATA_KEY]
        if (json != null) {
            try {
                gson.fromJson(json, LoginResponse::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Clear data on Logout
     */
    suspend fun clearUser() {
        context.userStore.edit { prefs ->
            prefs.remove(USER_DATA_KEY)
            prefs.remove(USER_TOKEN_KEY)
        }
    }
}