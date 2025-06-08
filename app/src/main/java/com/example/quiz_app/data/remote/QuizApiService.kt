package com.example.quiz_app.data.remote

import com.example.quiz_app.data.remote.dto.QuizDto
import retrofit2.http.GET
import retrofit2.http.Path

interface QuizApiService {
    
    @GET("api/v1/quizzes")
    suspend fun getQuizzes(): List<QuizDto>
    
    @GET("api/v1/quizzes/{id}")
    suspend fun getQuizById(@Path("id") id: String): QuizDto
}