package com.example.quiz_app.data.repository

import com.example.quiz_app.data.remote.QuizApiService
import com.example.quiz_app.data.remote.dto.toDomain
import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.Question
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
            val quizDtos = apiService.getQuizzes()
            val quizzes = quizDtos.map { it.toDomain() }
            emit(quizzes)
        } catch (e: Exception) {
            throw e
        }
    }
    
    override suspend fun getQuizById(id: String): Result<Quiz> {
        return Result.failure(NotImplementedError("getQuizById is not implemented yet."))
    }

    override suspend fun searchQuizzes(query: String): Result<List<Quiz>> {
        return Result.failure(NotImplementedError("searchQuizzes is not implemented yet."))
    }

    override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> {
        return Result.failure(NotImplementedError("getQuizzesByCategory is not implemented yet."))
    }

    override suspend fun getQuizDetail(quizId: String): Result<List<Question>> {
        return Result.failure(NotImplementedError("getQuizDetail is not implemented yet."))
    }
}