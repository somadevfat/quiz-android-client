package com.example.quiz_app.data.repository

import com.example.quiz_app.data.remote.QuizApiService
import com.example.quiz_app.data.remote.dto.toDomain
import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val apiService: QuizApiService
) : QuizRepository {
    
    override fun getQuizzes(): Flow<List<Quiz>> = flow {
        try {
            android.util.Log.d("QuizRepository", "API呼び出し開始: ${apiService}")
            val quizDtos = apiService.getQuizzes()
            android.util.Log.d("QuizRepository", "API呼び出し成功: ${quizDtos.size}件取得")
            val quizzes = quizDtos.map { it.toDomain() }
            emit(quizzes)
        } catch (e: Exception) {
            android.util.Log.e("QuizRepository", "API呼び出しエラー: ${e.message}", e)
            throw e
        }
    }
    
    override fun getQuizById(id: String): Flow<Quiz?> = flow {
        try {
            val quizDto = apiService.getQuizById(id)
            val quiz = quizDto.toDomain()
            emit(quiz)
        } catch (e: Exception) {
            emit(null)
        }
    }
}