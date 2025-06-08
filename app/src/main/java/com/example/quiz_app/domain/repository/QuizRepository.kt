package com.example.quiz_app.domain.repository

import com.example.quiz_app.domain.Quiz
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getQuizzes(): Flow<List<Quiz>>
    fun getQuizById(id: String): Flow<Quiz?>
}