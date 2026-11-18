package com.lokahe.backend.rovortex

import com.lokahe.backend.rovortex.BuildConfig

object Api {
    const val BASE_URL = BuildConfig.ROVORTEX_URL

    /** Browser flow start point, e.g. https://host/api/auth/oauth/github */
    fun authorizeUrl(provider: String) = "${BASE_URL}api/auth/oauth/$provider"
}
