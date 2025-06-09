package com.example.quiz_app.domain.repository

import com.example.quiz_app.domain.*
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Bookmark management
    fun getBookmarks(): Flow<List<Bookmark>>
    suspend fun addBookmark(questionId: String, quizId: String, notes: String? = null): Result<Unit>
    suspend fun removeBookmark(questionId: String): Result<Unit>
    fun isBookmarked(questionId: String): Flow<Boolean>
    suspend fun getBookmarkFolders(): Result<List<BookmarkFolder>>
    suspend fun createBookmarkFolder(name: String, description: String?): Result<BookmarkFolder>
    suspend fun addToFolder(bookmarkId: String, folderId: String): Result<Unit>

    // Learning history
    fun getLearningHistory(): Flow<List<LearningHistory>>
    suspend fun addLearningRecord(record: LearningHistory): Result<Unit>
    suspend fun getQuizSessions(): Result<List<QuizSession>>
    suspend fun startQuizSession(quizId: String, quizTitle: String): Result<QuizSession>
    suspend fun updateQuizSession(session: QuizSession): Result<Unit>
    suspend fun completeQuizSession(sessionId: String, answers: List<SessionAnswer>): Result<QuizSession>
    
    // Statistics
    suspend fun getLearningStatistics(): Result<LearningStatistics>
    suspend fun getCategoryStatistics(): Result<Map<String, CategoryStatistics>>
    suspend fun getDifficultyStatistics(): Result<Map<QuizDifficulty, DifficultyStatistics>>
    suspend fun getMonthlyProgress(): Result<List<MonthlyProgress>>
    suspend fun getWeeklyProgress(): Result<List<WeeklyProgress>>
    suspend fun getDailyStats(date: String): Result<DailyLearningStats>
    
    // Achievements
    suspend fun getAchievements(): Result<List<Achievement>>
    suspend fun unlockAchievement(achievementId: String): Result<Unit>
    suspend fun getPerformanceAnalysis(): Result<PerformanceAnalysis>
    
    // Search and filtering
    suspend fun saveSearch(search: SavedSearch): Result<Unit>
    suspend fun getSavedSearches(): Result<List<SavedSearch>>
    suspend fun getFilterOptions(): Result<FilterOptions>
    
    // User settings
    suspend fun updateUserPreferences(preferences: Map<String, Any>): Result<Unit>
    suspend fun getUserPreferences(): Result<Map<String, Any>>
}