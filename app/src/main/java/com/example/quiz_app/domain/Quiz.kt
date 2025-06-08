package com.example.quiz_app.domain

data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: QuizDifficulty,
    val categoryId: String,
    val categoryName: String,
    val questionCount: Int,
    val timeLimit: Int?, // in minutes, null if no time limit
    val imageUrl: String?,
    val createdAt: String,
    val updatedAt: String
)

enum class QuizDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}