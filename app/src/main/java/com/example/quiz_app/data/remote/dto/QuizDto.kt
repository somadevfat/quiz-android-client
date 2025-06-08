package com.example.quiz_app.data.remote.dto

import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuizDto(
    @Json(name = "id")
    val id: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "difficulty")
    val difficulty: String,
    @Json(name = "category_id")
    val categoryId: String,
    @Json(name = "category_name")
    val categoryName: String,
    @Json(name = "question_count")
    val questionCount: Int,
    @Json(name = "time_limit")
    val timeLimit: Int?,
    @Json(name = "image_url")
    val imageUrl: String?,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String
)

fun QuizDto.toDomain(): Quiz {
    return Quiz(
        id = id,
        title = title,
        description = description,
        difficulty = when (difficulty.lowercase()) {
            "beginner" -> QuizDifficulty.BEGINNER
            "intermediate" -> QuizDifficulty.INTERMEDIATE
            "advanced" -> QuizDifficulty.ADVANCED
            else -> QuizDifficulty.BEGINNER
        },
        categoryId = categoryId,
        categoryName = categoryName,
        questionCount = questionCount,
        timeLimit = timeLimit,
        imageUrl = imageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}