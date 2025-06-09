package com.example.quiz_app.di

import com.example.quiz_app.data.repository.FakeQuizRepository
import com.example.quiz_app.data.repository.MockUserRepository
import com.example.quiz_app.data.repository.QuizRepositoryImpl
import com.example.quiz_app.domain.repository.QuizRepository
import com.example.quiz_app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    // Use Mock/Fake repositories for development with extensive mock data
    @Binds
    abstract fun bindQuizRepository(
        fakeQuizRepository: FakeQuizRepository
    ): QuizRepository

    @Binds
    abstract fun bindUserRepository(
        mockUserRepository: MockUserRepository
    ): UserRepository
    
    // Uncomment below and comment above to use real API implementation
    // @Binds
    // abstract fun bindQuizRepository(
    //     quizRepositoryImpl: QuizRepositoryImpl
    // ): QuizRepository
}