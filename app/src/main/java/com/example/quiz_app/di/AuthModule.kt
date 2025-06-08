package com.example.quiz_app.di

import com.example.quiz_app.data.remote.AuthApiService
import com.example.quiz_app.data.repository.AuthRepositoryImpl
import com.example.quiz_app.data.repository.FakeAuthRepository
import com.example.quiz_app.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    // For testing/development, comment out the real implementation and uncomment this:
    // @Binds
    // @Singleton
    // abstract fun bindAuthRepository(
    //     fakeAuthRepository: FakeAuthRepository
    // ): AuthRepository
}