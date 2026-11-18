package com.lokahe.http.di

import com.lokahe.http.HttpConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Provides the generic HTTP stack (OkHttp + Retrofit).
 * The app module supplies its own [HttpConfig] (e.g. baseUrl from BuildConfig).
 */
@Module
@InstallIn(SingletonComponent::class)
object HttpModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(config: HttpConfig): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = config.loggingLevel
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        config: HttpConfig
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(config: HttpConfig, okHttpClient: OkHttpClient): Retrofit {
        // Fail fast with an actionable message instead of Retrofit's cryptic
        // "baseUrl must end in /" / empty-URL IllegalArgumentException (issue #3).
        require(config.baseUrl.isNotBlank()) {
            "HttpConfig.baseUrl is blank - set SPB_URL in local.properties or the SPB_URL " +
                    "environment variable (see issue #3)."
        }
        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}