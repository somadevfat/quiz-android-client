package com.example.quiz_app.di

import com.example.quiz_app.data.repository.QuizRepositoryImpl
import com.example.quiz_app.domain.repository.QuizRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindQuizRepository(
        quizRepositoryImpl: QuizRepositoryImpl
    ): QuizRepository
    
    // Uncomment below and comment above to use fake data for development
    // @Binds
    // abstract fun bindQuizRepository(
    //     fakeQuizRepository: FakeQuizRepository
    // ): QuizRepository
}