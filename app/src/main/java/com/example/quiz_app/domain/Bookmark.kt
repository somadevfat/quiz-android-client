package com.example.quiz_app.domain

import java.time.LocalDateTime

data class Bookmark(
    val id: String,
    val questionId: String,
    val quizId: String,
    val questionText: String,
    val category: String,
    val difficulty: QuizDifficulty,
    val createdAt: LocalDateTime,
    val notes: String? = null,
    val tags: List<String> = emptyList()
)

data class BookmarkFolder(
    val id: String,
    val name: String,
    val description: String? = null,
    val bookmarkIds: List<String> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)