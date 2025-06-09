package com.example.quiz_app.domain.repository

import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.Question
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getQuizzes(): Flow<List<Quiz>>
    suspend fun getQuizById(id: String): Result<Quiz>
    suspend fun searchQuizzes(query: String): Result<List<Quiz>>
    suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>>
    suspend fun getQuizDetail(quizId: String): Result<List<Question>>
}