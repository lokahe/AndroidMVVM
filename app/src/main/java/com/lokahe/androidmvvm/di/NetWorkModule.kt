package com.lokahe.androidmvvm.di

import com.lokahe.androidmvvm.data.remote.Api
import com.lokahe.androidmvvm.data.remote.ApiService
import com.lokahe.http.HttpConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetWorkModule {

    // App-specific network configuration; OkHttp / Retrofit are built in the :http module
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