package com.lokahe.androidmvvm.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.lokahe.androidmvvm.addOrRemove
import com.lokahe.androidmvvm.data.models.supabase.Follower
import com.lokahe.androidmvvm.data.models.supabase.Liked
import com.lokahe.androidmvvm.data.models.supabase.User
import com.lokahe.androidmvvm.ui.theme.ColorSeed
import com.lokahe.androidmvvm.utils.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
        private val USER_TOKEN_EXPIRES_AT_KEY = longPreferencesKey("user_token_expires_at")
        private val USER_REFRESH_TOKEN_KEY = stringPreferencesKey("user_refresh_token")
        private val USER_COLOR_SEED_KEY = stringPreferencesKey("user_color_seed")
        private val CODE_VERIFIER = stringPreferencesKey("code_verifier")
    }

    /**
     * Helper to get just the token
     */
    val accessTokenFlow: Flow<String?> = context.userStore.data.map { prefs ->
        prefs[USER_TOKEN_KEY]
    }

    val accessTokenExpiresAtFlow: Flow<Long?> = context.userStore.data.map { prefs ->
        prefs[USER_TOKEN_EXPIRES_AT_KEY]
    }

    val refreshTokenFlow: Flow<String?> = context.userStore.data.map { prefs ->
        prefs[USER_REFRESH_TOKEN_KEY]
    }

    val codeVerifierFlow: Flow<String?> = context.userStore.data.map { prefs ->
        prefs[CODE_VERIFIER]
    }

    /**
     * Retrieves the full User object as a Flow
     */
    val userFlow: Flow<User?> = context.userStore.data.map { prefs ->
        prefs[USER_DATA_KEY]?.let {
            try {
                gson.fromJson(it, User::class.java)
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
     * save tokens
     */
    suspend fun saveToken(accessToken: String?, expiresAt: Long?, refreshToken: String?) {
        context.userStore.edit { prefs ->
            accessToken?.let { prefs[USER_TOKEN_KEY] = it }
            expiresAt?.let { prefs[USER_TOKEN_EXPIRES_AT_KEY] = it }
            refreshToken?.let { prefs[USER_REFRESH_TOKEN_KEY] = it }
        }
    }

    suspend fun saveCodeVerifier(codeVerifier: String) {
        context.userStore.edit { prefs -> prefs[CODE_VERIFIER] = codeVerifier }
    }

    /**
     * save user
     */
    suspend fun saveUser(user: User?) {
        user?.let {
            Log.d("saveUser", "user: $user")
            context.userStore.edit { prefs ->
                prefs[USER_DATA_KEY] = gson.toJson(it)
                Utils.calculateMainColor(it.userMetadata?.avatarUrl)?.let { seed ->
                    prefs[USER_COLOR_SEED_KEY] = gson.toJson(seed)
                }
            }
        }
    }

    suspend fun updateProfileLocal(
        follower: Follower? = null,
        liked: Liked? = null
    ) {
        Log.d("updateProfileLocal", "follower: $follower, liked: $liked")
        userFlow.firstOrNull()?.let { user ->
            user.profile?.let { prof ->
                saveUser(
                    @Suppress("UNCHECKED_CAST")
                    user.copy(
                        profile = prof.copy(
                            followingList = follower?.let { prof.followingList.addOrRemove(it) as List<Follower> }
                                ?: prof.followingList,
                            likedList = liked?.let { prof.likedList.addOrRemove(it) as List<Liked> }
                                ?: prof.likedList
                        )
                    )
                )
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
            prefs.remove(USER_REFRESH_TOKEN_KEY)
            prefs.remove(USER_TOKEN_EXPIRES_AT_KEY)
            prefs.remove(USER_COLOR_SEED_KEY)
            prefs.remove(CODE_VERIFIER)
        }
    }
}