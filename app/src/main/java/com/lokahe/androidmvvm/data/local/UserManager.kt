package com.lokahe.androidmvvm.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.lokahe.androidmvvm.data.models.network.LoginResponse
import com.lokahe.androidmvvm.ui.theme.ColorSeed
import com.lokahe.androidmvvm.utils.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    companion object {
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        private val USER_COLOR_SEED_KEY = stringPreferencesKey("user_color_seed")
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
            response.userToken?.let { prefs[USER_TOKEN_KEY] = it }
            // 2. Save the entire object as a JSON string
            prefs[USER_DATA_KEY] = gson.toJson(response)
            // 3. calculate and save color seed
            response.avatar?.let {
                Utils.Companion.calculateMainColor(it)?.let { seed ->
                    prefs[USER_COLOR_SEED_KEY] = gson.toJson(seed)
                }
            }
        }
    }

    /**
     * Retrieves the full User object as a Flow
     */
    val userFlow: Flow<LoginResponse?> = context.userStore.data.map { prefs ->
        prefs[USER_DATA_KEY]?.let {
            try {
                gson.fromJson(it, LoginResponse::class.java)
            } catch (e: Exception) {
                Log.e("UserManager", "Error deserializing user data: ${e.message}")
                null
            }
        }
    }

    /**
     * Retrieves the ColorSeed object as a Flow
     */
    val colorSeedFlow: Flow<ColorSeed?> = context.userStore.data.map { prefs ->
        prefs[USER_COLOR_SEED_KEY]?.let {
            try {
                gson.fromJson(it, ColorSeed::class.java)
            } catch (e: Exception) {
                Log.e("UserManager", "Error deserializing ColorSeed: ${e.message}")
                null
            }
        }
    }

    /**
     * Clear data on Logout
     */
    suspend fun clearUser() {
        context.userStore.edit { prefs ->
            prefs.remove(USER_DATA_KEY)
            prefs.remove(USER_TOKEN_KEY)
            prefs.remove(USER_COLOR_SEED_KEY)
        }
    }
}