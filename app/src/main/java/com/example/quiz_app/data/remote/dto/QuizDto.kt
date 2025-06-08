package com.example.quiz_app.data.remote.dto

import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuizDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "qid")
    val qid: String,
    @Json(name = "chapter")
    val chapter: String,
    @Json(name = "category")
    val category: String,
    @Json(name = "difficulty")
    val difficulty: String,
    @Json(name = "choices")
    val choices: List<String>,
    @Json(name = "code")
    val code: String?,
    @Json(name = "questionText")
    val questionText: String,
    @Json(name = "explanation")
    val explanation: String,
    @Json(name = "questionCategory")
    val questionCategory: String
)

fun QuizDto.toDomain(): Quiz {
    return Quiz(
        id = id.toString(),
        title = "$chapter - $category",
        description = questionText,
        difficulty = when (difficulty) {
            "初級" -> QuizDifficulty.BEGINNER
            "中級" -> QuizDifficulty.INTERMEDIATE
            "上級" -> QuizDifficulty.ADVANCED
            else -> QuizDifficulty.BEGINNER
        },
        categoryId = qid,
        categoryName = category,
        questionCount = choices.size,
        timeLimit = null,
        imageUrl = null,
        createdAt = "",
        updatedAt = ""
    )
}