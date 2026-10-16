package com.lokahe.androidmvvm.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.lokahe.androidmvvm.addOrRemove
import com.lokahe.androidmvvm.ui.theme.ColorSeed
import com.lokahe.androidmvvm.utils.Utils
import com.lokahe.auth.PkceStore
import com.lokahe.auth.Session
import com.lokahe.auth.TokenStore
import com.lokahe.domain.model.Following
import com.lokahe.domain.model.User
import com.lokahe.log.L
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

/**
 * App-side session/profile facade.
 *
 * Token credentials and the PKCE verifier are delegated to the backend-neutral
 * [TokenStore] / [PkceStore] (:auth); this class only owns the user profile
 * JSON and UI-derived state (color seed). Swapping backends never touches it.
 */
@Singleton
class UserManager @Inject constructor(
    private val tokenStore: TokenStore,
    private val pkceStore: PkceStore,
    @Named("user_prefs") private val userStore: DataStore<Preferences>
) {
    private val gson = Gson()

    companion object {
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val USER_COLOR_SEED_KEY = stringPreferencesKey("user_color_seed")
    }

    // ---- session (delegates to :auth) ----

    suspend fun currentSession(): Session? = tokenStore.current()

    suspend fun saveSession(session: Session) = tokenStore.save(session)

    /** Keep the old partial-save semantics for existing call sites. */
    suspend fun saveToken(accessToken: String?, expiresAt: Long?, refreshToken: String?) {
        tokenStore.save(
            Session(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresAt = expiresAt
            )
        )
    }

    suspend fun saveCodeVerifier(codeVerifier: String) = pkceStore.save(codeVerifier)

    val codeVerifierFlow: Flow<String?> = pkceStore.codeVerifierFlow

    // ---- user profile ----

    /**
     * Retrieves the full User object as a Flow
     */
    val userFlow: Flow<User?> = userStore.data.map { prefs ->
        prefs[USER_DATA_KEY]?.let {
            try {
                gson.fromJson(it, User::class.java)
            } catch (e: Exception) {
                L.e("UserManager") { "Error deserializing user data: ${e.message}" }
                null
            }
        }
    }

    /**
     * Retrieves the ColorSeed object as a Flow
     */
    val colorSeedFlow: Flow<ColorSeed?> = userStore.data.map { prefs ->
        prefs[USER_COLOR_SEED_KEY]?.let {
            try {
                gson.fromJson(it, ColorSeed::class.java)
            } catch (e: Exception) {
                L.e("UserManager") { "Error deserializing ColorSeed: ${e.message}" }
                null
            }
        }
    }

    /**
     * save user
     */
    suspend fun saveUser(user: User?) {
        user?.let {
            L.d("saveUser") { "user: $user" }
            userStore.edit { prefs ->
                prefs[USER_DATA_KEY] = gson.toJson(it)
                Utils.calculateMainColor(it.avatarUrl)?.let { seed ->
                    prefs[USER_COLOR_SEED_KEY] = gson.toJson(seed)
                }
            }
        }
    }

    suspend fun updateProfileLocal(
        follower: Following? = null,
    ) {
        userFlow.firstOrNull()?.let { user ->
            user.profile?.let { prof ->
                saveUser(
                    @Suppress("UNCHECKED_CAST")
                    user.copy(
                        profile = prof.copy(
                            followingList = follower?.let { prof.followingList?.addOrRemove(it) as List<Following> }
                                ?: prof.followingList,
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
        tokenStore.clear()
        pkceStore.clear()
        userStore.edit { prefs ->
            prefs.remove(USER_DATA_KEY)
            prefs.remove(USER_COLOR_SEED_KEY)
        }
    }
}