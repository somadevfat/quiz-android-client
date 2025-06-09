package com.example.quiz_app.data.repository

import com.example.quiz_app.domain.*
import com.example.quiz_app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockUserRepository @Inject constructor() : UserRepository {

    private val mockBookmarks = mutableListOf(
        Bookmark(
            id = "bm1",
            questionId = "q1_1",
            quizId = "1",
            questionText = "Androidアプリケーションの基本構成要素として正しくないものは？",
            category = "Android Development",
            difficulty = QuizDifficulty.BEGINNER,
            createdAt = LocalDateTime.now().minusDays(5),
            notes = "重要な基礎概念",
            tags = listOf("基礎", "重要")
        ),
        Bookmark(
            id = "bm2",
            questionId = "q12_1",
            quizId = "12",
            questionText = "Kotlinのcoroutinesで非同期処理を開始するために使用する関数は？",
            category = "Kotlin Programming",
            difficulty = QuizDifficulty.ADVANCED,
            createdAt = LocalDateTime.now().minusDays(2),
            notes = "コルーチンの基本",
            tags = listOf("coroutines", "非同期")
        ),
        Bookmark(
            id = "bm3",
            questionId = "q23_1",
            quizId = "23",
            questionText = "Jetpack Composeの基本原則として正しいものは？",
            category = "Jetpack Compose",
            difficulty = QuizDifficulty.BEGINNER,
            createdAt = LocalDateTime.now().minusDays(1),
            notes = "Compose UI の基本",
            tags = listOf("compose", "UI")
        )
    )

    private val mockBookmarkFolders = mutableListOf(
        BookmarkFolder(
            id = "folder1",
            name = "Android基礎",
            description = "Android開発の基礎的な問題集",
            bookmarkIds = listOf("bm1"),
            createdAt = LocalDateTime.now().minusDays(10),
            updatedAt = LocalDateTime.now().minusDays(5)
        ),
        BookmarkFolder(
            id = "folder2", 
            name = "Kotlin上級",
            description = "Kotlinの高度な概念",
            bookmarkIds = listOf("bm2"),
            createdAt = LocalDateTime.now().minusDays(7),
            updatedAt = LocalDateTime.now().minusDays(2)
        )
    )

    private val mockLearningHistory = mutableListOf(
        LearningHistory(
            id = "history1",
            userId = "user1",
            quizId = "1",
            quizTitle = "Android Development Fundamentals",
            questionId = "q1_1",
            questionText = "Androidアプリケーションの基本構成要素として正しくないものは？",
            selectedAnswer = 2,
            correctAnswer = 2,
            isCorrect = true,
            timeSpent = 45000, // 45 seconds
            answeredAt = LocalDateTime.now().minusHours(2),
            category = "Android Development",
            difficulty = QuizDifficulty.BEGINNER
        ),
        LearningHistory(
            id = "history2",
            userId = "user1",
            quizId = "12",
            quizTitle = "Kotlin Advanced Features",
            questionId = "q12_1",
            questionText = "Kotlinのcoroutinesで非同期処理を開始するために使用する関数は？",
            selectedAnswer = 0,
            correctAnswer = 1,
            isCorrect = false,
            timeSpent = 62000, // 62 seconds
            answeredAt = LocalDateTime.now().minusHours(1),
            category = "Kotlin Programming",
            difficulty = QuizDifficulty.ADVANCED
        ),
        LearningHistory(
            id = "history3",
            userId = "user1",
            quizId = "23",
            quizTitle = "Jetpack Compose Basics",
            questionId = "q23_1",
            questionText = "Jetpack Composeの基本原則として正しいものは？",
            selectedAnswer = 1,
            correctAnswer = 1,
            isCorrect = true,
            timeSpent = 38000, // 38 seconds
            answeredAt = LocalDateTime.now().minusMinutes(30),
            category = "Jetpack Compose",
            difficulty = QuizDifficulty.BEGINNER
        )
    )

    private val mockQuizSessions = mutableListOf(
        QuizSession(
            id = "session1",
            userId = "user1",
            quizId = "1",
            quizTitle = "Android Development Fundamentals",
            startedAt = LocalDateTime.now().minusHours(3),
            completedAt = LocalDateTime.now().minusHours(2),
            totalQuestions = 5,
            answeredQuestions = 5,
            correctAnswers = 4,
            totalTimeSpent = 600000, // 10 minutes
            scorePercentage = 80.0f,
            status = QuizSessionStatus.COMPLETED,
            answers = listOf(
                SessionAnswer(
                    questionId = "q1_1",
                    selectedAnswer = 2,
                    correctAnswer = 2,
                    isCorrect = true,
                    timeSpent = 45000,
                    answeredAt = LocalDateTime.now().minusHours(2)
                )
            )
        ),
        QuizSession(
            id = "session2",
            userId = "user1",
            quizId = "12",
            quizTitle = "Kotlin Advanced Features",
            startedAt = LocalDateTime.now().minusMinutes(45),
            completedAt = null,
            totalQuestions = 3,
            answeredQuestions = 2,
            correctAnswers = 1,
            totalTimeSpent = 300000, // 5 minutes
            scorePercentage = 50.0f,
            status = QuizSessionStatus.IN_PROGRESS
        )
    )

    private val mockAchievements = listOf(
        Achievement(
            id = "ach1",
            title = "初回回答",
            description = "初めて問題に回答しました",
            type = AchievementType.QUESTIONS_ANSWERED,
            icon = "first_answer",
            unlockedAt = LocalDateTime.now().minusDays(5),
            progress = 1,
            maxProgress = 1,
            isUnlocked = true
        ),
        Achievement(
            id = "ach2",
            title = "Android マスター",
            description = "Android問題を10問正解",
            type = AchievementType.CATEGORY_MASTER,
            icon = "android_master",
            unlockedAt = LocalDateTime.now().minusDays(2),
            progress = 10,
            maxProgress = 10,
            isUnlocked = true
        ),
        Achievement(
            id = "ach3",
            title = "週末戦士",
            description = "7日連続で学習",
            type = AchievementType.STREAK,
            icon = "streak_warrior",
            unlockedAt = null,
            progress = 5,
            maxProgress = 7,
            isUnlocked = false
        ),
        Achievement(
            id = "ach4",
            title = "正確性の達人",
            description = "90%以上の正解率を達成",
            type = AchievementType.ACCURACY,
            icon = "accuracy_master",
            unlockedAt = null,
            progress = 85,
            maxProgress = 90,
            isUnlocked = false
        )
    )

    private val mockSavedSearches = mutableListOf(
        SavedSearch(
            id = "search1",
            name = "Android基礎",
            criteria = SearchCriteria(
                query = "Android",
                categories = listOf("android"),
                difficulties = listOf(QuizDifficulty.BEGINNER),
                sortBy = SortOption.DIFFICULTY
            ),
            createdAt = LocalDateTime.now().minusDays(3),
            lastUsed = LocalDateTime.now().minusHours(2),
            useCount = 5
        ),
        SavedSearch(
            id = "search2",
            name = "Kotlin上級",
            criteria = SearchCriteria(
                query = "coroutines",
                categories = listOf("kotlin"),
                difficulties = listOf(QuizDifficulty.ADVANCED),
                sortBy = SortOption.CREATION_DATE
            ),
            createdAt = LocalDateTime.now().minusDays(1),
            lastUsed = LocalDateTime.now().minusMinutes(30),
            useCount = 2
        )
    )

    override fun getBookmarks(): Flow<List<Bookmark>> {
        return flowOf(mockBookmarks.toList())
    }

    override suspend fun addBookmark(questionId: String, quizId: String, notes: String?): Result<Unit> {
        val newBookmark = Bookmark(
            id = "bm${mockBookmarks.size + 1}",
            questionId = questionId,
            quizId = quizId,
            questionText = "Sample question text",
            category = "General",
            difficulty = QuizDifficulty.INTERMEDIATE,
            createdAt = LocalDateTime.now(),
            notes = notes,
            tags = emptyList()
        )
        mockBookmarks.add(newBookmark)
        return Result.success(Unit)
    }

    override suspend fun removeBookmark(questionId: String): Result<Unit> {
        mockBookmarks.removeAll { it.questionId == questionId }
        return Result.success(Unit)
    }

    override fun isBookmarked(questionId: String): Flow<Boolean> {
        return flowOf(mockBookmarks.any { it.questionId == questionId })
    }

    override suspend fun getBookmarkFolders(): Result<List<BookmarkFolder>> {
        return Result.success(mockBookmarkFolders.toList())
    }

    override suspend fun createBookmarkFolder(name: String, description: String?): Result<BookmarkFolder> {
        val newFolder = BookmarkFolder(
            id = "folder${mockBookmarkFolders.size + 1}",
            name = name,
            description = description,
            bookmarkIds = emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        mockBookmarkFolders.add(newFolder)
        return Result.success(newFolder)
    }

    override suspend fun addToFolder(bookmarkId: String, folderId: String): Result<Unit> {
        val folderIndex = mockBookmarkFolders.indexOfFirst { it.id == folderId }
        if (folderIndex != -1) {
            val folder = mockBookmarkFolders[folderIndex]
            val updatedFolder = folder.copy(
                bookmarkIds = folder.bookmarkIds + bookmarkId,
                updatedAt = LocalDateTime.now()
            )
            mockBookmarkFolders[folderIndex] = updatedFolder
        }
        return Result.success(Unit)
    }

    override fun getLearningHistory(): Flow<List<LearningHistory>> {
        return flowOf(mockLearningHistory.toList().sortedByDescending { it.answeredAt })
    }

    override suspend fun addLearningRecord(record: LearningHistory): Result<Unit> {
        mockLearningHistory.add(record)
        return Result.success(Unit)
    }

    override suspend fun getQuizSessions(): Result<List<QuizSession>> {
        return Result.success(mockQuizSessions.toList().sortedByDescending { it.startedAt })
    }

    override suspend fun startQuizSession(quizId: String, quizTitle: String): Result<QuizSession> {
        val newSession = QuizSession(
            id = "session${mockQuizSessions.size + 1}",
            userId = "user1",
            quizId = quizId,
            quizTitle = quizTitle,
            startedAt = LocalDateTime.now(),
            completedAt = null,
            totalQuestions = 0,
            answeredQuestions = 0,
            correctAnswers = 0,
            totalTimeSpent = 0,
            scorePercentage = 0.0f,
            status = QuizSessionStatus.IN_PROGRESS
        )
        mockQuizSessions.add(newSession)
        return Result.success(newSession)
    }

    override suspend fun updateQuizSession(session: QuizSession): Result<Unit> {
        val index = mockQuizSessions.indexOfFirst { it.id == session.id }
        if (index != -1) {
            mockQuizSessions[index] = session
        }
        return Result.success(Unit)
    }

    override suspend fun completeQuizSession(sessionId: String, answers: List<SessionAnswer>): Result<QuizSession> {
        val sessionIndex = mockQuizSessions.indexOfFirst { it.id == sessionId }
        if (sessionIndex != -1) {
            val session = mockQuizSessions[sessionIndex]
            val completedSession = session.copy(
                completedAt = LocalDateTime.now(),
                answeredQuestions = answers.size,
                correctAnswers = answers.count { it.isCorrect },
                scorePercentage = (answers.count { it.isCorrect }.toFloat() / answers.size) * 100,
                status = QuizSessionStatus.COMPLETED,
                answers = answers
            )
            mockQuizSessions[sessionIndex] = completedSession
            return Result.success(completedSession)
        }
        return Result.failure(Exception("Session not found"))
    }

    override suspend fun getLearningStatistics(): Result<LearningStatistics> {
        val totalQuestions = mockLearningHistory.size
        val correctAnswers = mockLearningHistory.count { it.isCorrect }
        val totalQuizzes = mockQuizSessions.count { it.status == QuizSessionStatus.COMPLETED }
        val totalTime = mockLearningHistory.sumOf { it.timeSpent }
        
        val categoryStats = mockLearningHistory.groupBy { it.category }.mapValues { (category, records) ->
            CategoryStatistics(
                category = category,
                questionsAnswered = records.size,
                correctAnswers = records.count { it.isCorrect },
                accuracy = records.count { it.isCorrect }.toFloat() / records.size,
                averageTimePerQuestion = records.map { it.timeSpent }.average().toLong(),
                totalTimeSpent = records.sumOf { it.timeSpent },
                quizzesCompleted = mockQuizSessions.count { it.quizTitle.contains(category) && it.status == QuizSessionStatus.COMPLETED },
                lastStudied = records.maxOfOrNull { it.answeredAt }
            )
        }

        val difficultyStats = mockLearningHistory.groupBy { it.difficulty }.mapValues { (difficulty, records) ->
            DifficultyStatistics(
                difficulty = difficulty,
                questionsAnswered = records.size,
                correctAnswers = records.count { it.isCorrect },
                accuracy = records.count { it.isCorrect }.toFloat() / records.size,
                averageTimePerQuestion = records.map { it.timeSpent }.average().toLong(),
                quizzesCompleted = mockQuizSessions.count { session ->
                    session.status == QuizSessionStatus.COMPLETED // Simplified logic
                }
            )
        }

        val monthlyProgress = listOf(
            MonthlyProgress(
                month = "2025-06",
                questionsAnswered = totalQuestions,
                correctAnswers = correctAnswers,
                quizzesCompleted = totalQuizzes,
                totalTimeSpent = totalTime,
                averageScore = if (totalQuestions > 0) (correctAnswers.toFloat() / totalQuestions) * 100 else 0.0f,
                studyDays = 5
            )
        )

        val statistics = LearningStatistics(
            userId = "user1",
            totalQuestionsAnswered = totalQuestions,
            totalCorrectAnswers = correctAnswers,
            totalQuizzesCompleted = totalQuizzes,
            totalTimeSpent = totalTime,
            overallAccuracy = if (totalQuestions > 0) (correctAnswers.toFloat() / totalQuestions) else 0.0f,
            averageTimePerQuestion = if (totalQuestions > 0) totalTime / totalQuestions else 0L,
            currentStreak = 3,
            longestStreak = 7,
            lastStudyDate = mockLearningHistory.maxOfOrNull { it.answeredAt },
            categoryStats = categoryStats,
            difficultyStats = difficultyStats,
            monthlyProgress = monthlyProgress,
            achievements = mockAchievements
        )

        return Result.success(statistics)
    }

    override suspend fun getCategoryStatistics(): Result<Map<String, CategoryStatistics>> {
        val categoryStats = mockLearningHistory.groupBy { it.category }.mapValues { (category, records) ->
            CategoryStatistics(
                category = category,
                questionsAnswered = records.size,
                correctAnswers = records.count { it.isCorrect },
                accuracy = records.count { it.isCorrect }.toFloat() / records.size,
                averageTimePerQuestion = records.map { it.timeSpent }.average().toLong(),
                totalTimeSpent = records.sumOf { it.timeSpent },
                quizzesCompleted = mockQuizSessions.count { it.quizTitle.contains(category) && it.status == QuizSessionStatus.COMPLETED },
                lastStudied = records.maxOfOrNull { it.answeredAt }
            )
        }
        return Result.success(categoryStats)
    }

    override suspend fun getDifficultyStatistics(): Result<Map<QuizDifficulty, DifficultyStatistics>> {
        val difficultyStats = mockLearningHistory.groupBy { it.difficulty }.mapValues { (difficulty, records) ->
            DifficultyStatistics(
                difficulty = difficulty,
                questionsAnswered = records.size,
                correctAnswers = records.count { it.isCorrect },
                accuracy = records.count { it.isCorrect }.toFloat() / records.size,
                averageTimePerQuestion = records.map { it.timeSpent }.average().toLong(),
                quizzesCompleted = 1 // Simplified
            )
        }
        return Result.success(difficultyStats)
    }

    override suspend fun getMonthlyProgress(): Result<List<MonthlyProgress>> {
        val monthlyProgress = listOf(
            MonthlyProgress(
                month = "2025-05",
                questionsAnswered = 45,
                correctAnswers = 38,
                quizzesCompleted = 8,
                totalTimeSpent = 2700000, // 45 minutes
                averageScore = 84.4f,
                studyDays = 12
            ),
            MonthlyProgress(
                month = "2025-06",
                questionsAnswered = mockLearningHistory.size,
                correctAnswers = mockLearningHistory.count { it.isCorrect },
                quizzesCompleted = mockQuizSessions.count { it.status == QuizSessionStatus.COMPLETED },
                totalTimeSpent = mockLearningHistory.sumOf { it.timeSpent },
                averageScore = if (mockLearningHistory.isNotEmpty()) (mockLearningHistory.count { it.isCorrect }.toFloat() / mockLearningHistory.size) * 100 else 0.0f,
                studyDays = 5
            )
        )
        return Result.success(monthlyProgress)
    }

    override suspend fun getWeeklyProgress(): Result<List<WeeklyProgress>> {
        val dailyStats = listOf(
            DailyLearningStats(
                date = LocalDateTime.now().minusDays(6),
                questionsAnswered = 5,
                correctAnswers = 4,
                totalTimeSpent = 300000,
                quizzesCompleted = 1,
                averageScore = 80.0f,
                studyStreak = 1
            ),
            DailyLearningStats(
                date = LocalDateTime.now().minusDays(5),
                questionsAnswered = 8,
                correctAnswers = 7,
                totalTimeSpent = 450000,
                quizzesCompleted = 1,
                averageScore = 87.5f,
                studyStreak = 2
            )
        )

        val weeklyProgress = listOf(
            WeeklyProgress(
                weekStart = LocalDateTime.now().minusDays(7),
                dailyStats = dailyStats,
                weeklyTotal = DailyLearningStats(
                    date = LocalDateTime.now(),
                    questionsAnswered = dailyStats.sumOf { it.questionsAnswered },
                    correctAnswers = dailyStats.sumOf { it.correctAnswers },
                    totalTimeSpent = dailyStats.sumOf { it.totalTimeSpent },
                    quizzesCompleted = dailyStats.sumOf { it.quizzesCompleted },
                    averageScore = dailyStats.map { it.averageScore }.average().toFloat(),
                    studyStreak = dailyStats.maxOf { it.studyStreak }
                ),
                improvement = 15.0f // 15% improvement from previous week
            )
        )
        return Result.success(weeklyProgress)
    }

    override suspend fun getDailyStats(date: String): Result<DailyLearningStats> {
        val dailyStats = DailyLearningStats(
            date = LocalDateTime.now(),
            questionsAnswered = mockLearningHistory.size,
            correctAnswers = mockLearningHistory.count { it.isCorrect },
            totalTimeSpent = mockLearningHistory.sumOf { it.timeSpent },
            quizzesCompleted = 1,
            averageScore = if (mockLearningHistory.isNotEmpty()) (mockLearningHistory.count { it.isCorrect }.toFloat() / mockLearningHistory.size) * 100 else 0.0f,
            studyStreak = 3
        )
        return Result.success(dailyStats)
    }

    override suspend fun getAchievements(): Result<List<Achievement>> {
        return Result.success(mockAchievements)
    }

    override suspend fun unlockAchievement(achievementId: String): Result<Unit> {
        // Mock implementation - would typically check conditions and unlock achievement
        return Result.success(Unit)
    }

    override suspend fun getPerformanceAnalysis(): Result<PerformanceAnalysis> {
        val analysis = PerformanceAnalysis(
            strongCategories = listOf("Android Development", "Jetpack Compose"),
            weakCategories = listOf("Kotlin Programming", "Testing"),
            recommendedStudyTime = 1800000, // 30 minutes
            nextGoals = listOf(
                "Kotlin Coroutines マスター",
                "Testing戦略の理解",
                "Clean Architecture実践"
            ),
            improvementSuggestions = listOf(
                "Kotlinの非同期処理をより深く学習",
                "実際のプロジェクトでテストコードを書く練習",
                "アーキテクチャパターンの実装例を研究"
            ),
            studyPattern = StudyPattern(
                preferredStudyTime = "evening",
                averageSessionLength = 600000, // 10 minutes
                studyFrequency = "daily",
                strongestDifficulty = QuizDifficulty.BEGINNER,
                mostActiveCategory = "Android Development"
            )
        )
        return Result.success(analysis)
    }

    override suspend fun saveSearch(search: SavedSearch): Result<Unit> {
        mockSavedSearches.add(search)
        return Result.success(Unit)
    }

    override suspend fun getSavedSearches(): Result<List<SavedSearch>> {
        return Result.success(mockSavedSearches.toList().sortedByDescending { it.lastUsed })
    }

    override suspend fun getFilterOptions(): Result<FilterOptions> {
        val filterOptions = FilterOptions(
            availableCategories = listOf(
                "Android Development",
                "Kotlin Programming", 
                "Jetpack Compose",
                "Software Architecture",
                "UI/UX Design",
                "Testing"
            ),
            availableChapters = listOf(
                "Chapter 1: Fundamentals",
                "Chapter 2: Intermediate Concepts",
                "Chapter 3: Advanced Topics",
                "Chapter 4: Best Practices"
            ),
            difficultyRange = QuizDifficulty.values().toList(),
            questionCountRange = 1..50,
            timeLimitRange = 5..120 // minutes
        )
        return Result.success(filterOptions)
    }

    override suspend fun updateUserPreferences(preferences: Map<String, Any>): Result<Unit> {
        // Mock implementation - would typically save to persistent storage
        return Result.success(Unit)
    }

    override suspend fun getUserPreferences(): Result<Map<String, Any>> {
        val preferences = mapOf(
            "theme" to "dark",
            "notifications" to true,
            "studyReminders" to true,
            "soundEffects" to false,
            "dailyGoal" to 10, // questions per day
            "preferredDifficulty" to "intermediate",
            "autoBookmark" to false
        )
        return Result.success(preferences)
    }
}