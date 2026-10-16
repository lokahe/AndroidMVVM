package com.lokahe.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * DataStore-backed [TokenStore].
 *
 * Uses the shared "user_prefs" store (provided by the host app as
 * @Named("user_prefs") DataStore<Preferences>) with the same key names the app
 * has always used, so existing installs keep their session across the split.
 */
class DataStoreTokenStore @Inject constructor(
    @Named("user_prefs") private val dataStore: DataStore<Preferences>
) : TokenStore {

    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val ACCESS_TOKEN = stringPreferencesKey("user_token")
        val ACCESS_TOKEN_EXPIRES_AT = longPreferencesKey("user_token_expires_at")
        val REFRESH_TOKEN = stringPreferencesKey("user_refresh_token")
    }

    override val sessionFlow: Flow<Session?> = dataStore.data.map { prefs ->
        val userId = prefs[Keys.USER_ID]
        val accessToken = prefs[Keys.ACCESS_TOKEN]
        val expiresAt = prefs[Keys.ACCESS_TOKEN_EXPIRES_AT]
        val refreshToken = prefs[Keys.REFRESH_TOKEN]
        if (userId == null && accessToken == null && expiresAt == null && refreshToken == null) {
            null
        } else {
            Session(userId, accessToken, refreshToken, expiresAt)
        }
    }

    override suspend fun current(): Session? = sessionFlow.firstOrNull()

    override suspend fun save(session: Session) {
        dataStore.edit { prefs ->
            session.userId?.let { prefs[Keys.USER_ID] = it }
            session.accessToken?.let { prefs[Keys.ACCESS_TOKEN] = it }
            session.refreshToken?.let { prefs[Keys.REFRESH_TOKEN] = it }
            session.expiresAt?.let { prefs[Keys.ACCESS_TOKEN_EXPIRES_AT] = it }
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
            prefs.remove(Keys.ACCESS_TOKEN_EXPIRES_AT)
        }
    }
}