package com.lokahe.backend.rovortex

import com.google.gson.Gson
import com.lokahe.backend.rovortex.model.AuthResponse

/** Shared Gson instance for parsing OAuth callback payloads. */
object GsonProvider {
    private val gson = Gson()

    /**
     * Parse the callback JSON; returns null for error envelopes
     * ({"error":{...}}) or malformed input — never throws.
     */
    fun authResponse(body: String): AuthResponse? = runCatching {
        val response = gson.fromJson(body, AuthResponse::class.java) ?: return null
        if (response.token.isNullOrEmpty()) null else response
    }.getOrNull()
}
