package com.lokahe.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/** DataStore-backed [PkceStore] (shared "user_prefs" store, legacy key kept). */
class DataStorePkceStore @Inject constructor(
    @Named("user_prefs") private val dataStore: DataStore<Preferences>
) : PkceStore {

    private val CODE_VERIFIER = stringPreferencesKey("code_verifier")

    override val codeVerifierFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[CODE_VERIFIER]
    }

    override suspend fun current(): String? = codeVerifierFlow.firstOrNull()

    override suspend fun save(codeVerifier: String) {
        dataStore.edit { prefs -> prefs[CODE_VERIFIER] = codeVerifier }
    }

    override suspend fun clear() {
        dataStore.edit { prefs -> prefs.remove(CODE_VERIFIER) }
    }
}