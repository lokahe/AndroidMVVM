package com.lokahe.backend.supabase.di

import com.lokahe.backend.supabase.Api
import com.lokahe.backend.supabase.ApiService
import com.lokahe.backend.supabase.SupabaseRepository
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
 * The ONLY backend-specific wiring the app needs. Swapping Supabase for
 * another backend = replacing this module (and the implementation classes)
 * — ViewModels, UI and the domain layer stay untouched.
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseProvidesModule {

    // Backend endpoint configuration; OkHttp / Retrofit are built in the :http module
    @Provides
    @Singleton
    fun provideHttpConfig(): HttpConfig {
        return HttpConfig(baseUrl = Api.SPB_URL)
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SupabaseBindsModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: SupabaseRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: SupabaseRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: SupabaseRepository): PostRepository
}