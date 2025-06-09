package com.example.quiz_app.domain

import java.time.LocalDateTime

data class LearningHistory(
    val id: String,
    val userId: String,
    val quizId: String,
    val quizTitle: String,
    val questionId: String,
    val questionText: String,
    val selectedAnswer: Int,
    val correctAnswer: Int,
    val isCorrect: Boolean,
    val timeSpent: Long, // milliseconds
    val answeredAt: LocalDateTime,
    val category: String,
    val difficulty: QuizDifficulty
)

data class QuizSession(
    val id: String,
    val userId: String,
    val quizId: String,
    val quizTitle: String,
    val startedAt: LocalDateTime,
    val completedAt: LocalDateTime? = null,
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val correctAnswers: Int,
    val totalTimeSpent: Long, // milliseconds
    val scorePercentage: Float,
    val status: QuizSessionStatus,
    val answers: List<SessionAnswer> = emptyList()
)

data class SessionAnswer(
    val questionId: String,
    val selectedAnswer: Int,
    val correctAnswer: Int,
    val isCorrect: Boolean,
    val timeSpent: Long,
    val answeredAt: LocalDateTime
)

enum class QuizSessionStatus {
    IN_PROGRESS,
    COMPLETED,
    ABANDONED,
    PAUSED
}

data class DailyLearningStats(
    val date: LocalDateTime,
    val questionsAnswered: Int,
    val correctAnswers: Int,
    val totalTimeSpent: Long,
    val quizzesCompleted: Int,
    val averageScore: Float,
    val studyStreak: Int
)