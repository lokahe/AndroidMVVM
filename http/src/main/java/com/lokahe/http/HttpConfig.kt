package com.lokahe.http

import okhttp3.logging.HttpLoggingInterceptor

/**
 * Network-only configuration. Provided by the app module (e.g. baseUrl from BuildConfig);
 * contains no business logic.
 */
data class HttpConfig(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 30,
    val readTimeoutSeconds: Long = 30,
    val writeTimeoutSeconds: Long = 30,
    val loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
)