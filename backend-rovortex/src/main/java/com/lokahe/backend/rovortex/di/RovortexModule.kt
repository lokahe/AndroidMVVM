package com.lokahe.backend.rovortex.di

import com.lokahe.backend.rovortex.Api
import com.lokahe.backend.rovortex.ApiService
import com.lokahe.backend.rovortex.RovortexRepository
import com.lokahe.domain.repository.AuthRepository
import com.lokahe.domain.repository.PostRepository
import com.lokahe.domain.repository.UserRepository
import com.lokahe.http.HttpConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * The ONLY backend-specific wiring the app needs. Swapping backends =
 * replacing this module (and the implementation classes) — ViewModels, UI
 * and the domain layer stay untouched.
 */
@Module
@InstallIn(SingletonComponent::class)
object RovortexProvidesModule {

    // Backend endpoint configuration; OkHttp / Retrofit are built in the :http module
    @Provides
    @Singleton
    fun provideHttpConfig(): HttpConfig {
        return HttpConfig(baseUrl = Api.BASE_URL)
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RovortexBindsModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: RovortexRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: RovortexRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: RovortexRepository): PostRepository
}
