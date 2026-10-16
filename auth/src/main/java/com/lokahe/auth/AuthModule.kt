package com.lokahe.auth

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindsModule {

    @Binds
    @Singleton
    abstract fun bindTokenStore(impl: DataStoreTokenStore): TokenStore

    @Binds
    @Singleton
    abstract fun bindPkceStore(impl: DataStorePkceStore): PkceStore
}

@Module
@InstallIn(SingletonComponent::class)
object AuthProviderModule {

    /**
     * Concrete providers are bound with @Named qualifiers; new platforms
     * (apple, wechat, ...) simply add another @Provides here.
     */
    @Provides
    @Singleton
    @Named("google")
    fun provideGoogleAuthProvider(): AuthProvider =
        GoogleAuthProvider(BuildConfig.GOOGLE_WEB_CLIENT_ID)
}