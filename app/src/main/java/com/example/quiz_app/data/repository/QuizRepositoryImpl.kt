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
        return try {
            val quizDto = apiService.getQuizById(id)
            Result.success(quizDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchQuizzes(query: String): Result<List<Quiz>> {
        return try {
            val quizDtos = apiService.getQuizzes()
            val allQuizzes = quizDtos.map { it.toDomain() }
            val filteredQuizzes = allQuizzes.filter { quiz ->
                quiz.title.contains(query, ignoreCase = true) ||
                quiz.description.contains(query, ignoreCase = true) ||
                quiz.categoryName.contains(query, ignoreCase = true)
            }
            Result.success(filteredQuizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> {
        return try {
            val quizDtos = apiService.getQuizzes()
            val allQuizzes = quizDtos.map { it.toDomain() }
            val categoryQuizzes = allQuizzes.filter { quiz ->
                quiz.categoryId == categoryId
            }
            Result.success(categoryQuizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuizDetail(quizId: String): Result<List<Question>> {
        return try {
            val quizDto = apiService.getQuizById(quizId)
            // Convert QuizDto to Question list (one question per quiz for now)
            val question = Question(
                id = "q_${quizDto.id}",
                questionText = quizDto.questionText,
                options = quizDto.choices,
                correctAnswerIndex = 1, // Assuming index 1 for now
                explanation = quizDto.explanation
            )
            Result.success(listOf(question))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}