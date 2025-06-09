package com.example.quiz_app.domain

import java.time.LocalDateTime

data class LearningStatistics(
    val userId: String,
    val totalQuestionsAnswered: Int,
    val totalCorrectAnswers: Int,
    val totalQuizzesCompleted: Int,
    val totalTimeSpent: Long, // milliseconds
    val overallAccuracy: Float,
    val averageTimePerQuestion: Long,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastStudyDate: LocalDateTime?,
    val categoryStats: Map<String, CategoryStatistics>,
    val difficultyStats: Map<QuizDifficulty, DifficultyStatistics>,
    val monthlyProgress: List<MonthlyProgress>,
    val achievements: List<Achievement>
)

data class CategoryStatistics(
    val category: String,
    val questionsAnswered: Int,
    val correctAnswers: Int,
    val accuracy: Float,
    val averageTimePerQuestion: Long,
    val totalTimeSpent: Long,
    val quizzesCompleted: Int,
    val lastStudied: LocalDateTime?
)

data class DifficultyStatistics(
    val difficulty: QuizDifficulty,
    val questionsAnswered: Int,
    val correctAnswers: Int,
    val accuracy: Float,
    val averageTimePerQuestion: Long,
    val quizzesCompleted: Int
)

data class MonthlyProgress(
    val month: String, // "2025-06"
    val questionsAnswered: Int,
    val correctAnswers: Int,
    val quizzesCompleted: Int,
    val totalTimeSpent: Long,
    val averageScore: Float,
    val studyDays: Int
)

data class WeeklyProgress(
    val weekStart: LocalDateTime,
    val dailyStats: List<DailyLearningStats>,
    val weeklyTotal: DailyLearningStats,
    val improvement: Float // percentage change from previous week
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val type: AchievementType,
    val icon: String,
    val unlockedAt: LocalDateTime?,
    val progress: Int,
    val maxProgress: Int,
    val isUnlocked: Boolean
)

enum class AchievementType {
    QUESTIONS_ANSWERED,
    QUIZ_COMPLETED,
    STREAK,
    ACCURACY,
    CATEGORY_MASTER,
    TIME_BASED,
    SPECIAL
}

data class PerformanceAnalysis(
    val strongCategories: List<String>,
    val weakCategories: List<String>,
    val recommendedStudyTime: Long,
    val nextGoals: List<String>,
    val improvementSuggestions: List<String>,
    val studyPattern: StudyPattern
)

data class StudyPattern(
    val preferredStudyTime: String, // "morning", "afternoon", "evening"
    val averageSessionLength: Long,
    val studyFrequency: String, // "daily", "weekdays", "weekends"
    val strongestDifficulty: QuizDifficulty,
    val mostActiveCategory: String
)