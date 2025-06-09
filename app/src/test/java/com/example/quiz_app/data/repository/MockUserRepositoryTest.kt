package com.example.quiz_app.data.repository

import com.example.quiz_app.domain.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import java.time.LocalDateTime

class MockUserRepositoryTest {

    private lateinit var repository: MockUserRepository

    @BeforeEach
    fun setup() {
        repository = MockUserRepository()
    }

    @AfterEach
    fun tearDown() {
        // Clean up if needed
    }

    @Nested
    @DisplayName("Bookmark Management Tests")
    inner class BookmarkTests {

        @Test
        @DisplayName("Should return initial bookmarks")
        fun `getBookmarks returns initial bookmarks`() = runTest {
            // When
            val bookmarks = repository.getBookmarks().first()

            // Then
            assertEquals(3, bookmarks.size)
            assertTrue(bookmarks.any { it.questionId == "q1_1" })
            assertTrue(bookmarks.any { it.questionId == "q12_1" })
            assertTrue(bookmarks.any { it.questionId == "q23_1" })
        }

        @Test
        @DisplayName("Should add bookmark successfully")
        fun `addBookmark adds new bookmark successfully`() = runTest {
            // Given
            val questionId = "q_new_1"
            val quizId = "quiz_new"
            val notes = "Important new concept"

            // When
            val result = repository.addBookmark(questionId, quizId, notes)
            val bookmarks = repository.getBookmarks().first()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(4, bookmarks.size)
            assertTrue(bookmarks.any { it.questionId == questionId })
        }

        @Test
        @DisplayName("Should remove bookmark successfully")
        fun `removeBookmark removes existing bookmark`() = runTest {
            // Given
            val questionIdToRemove = "q1_1"

            // When
            val result = repository.removeBookmark(questionIdToRemove)
            val bookmarks = repository.getBookmarks().first()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(2, bookmarks.size)
            assertFalse(bookmarks.any { it.questionId == questionIdToRemove })
        }

        @Test
        @DisplayName("Should check if question is bookmarked")
        fun `isBookmarked returns correct status`() = runTest {
            // Given
            val existingQuestionId = "q1_1"
            val nonExistingQuestionId = "q_not_exists"

            // When
            val isBookmarkedExisting = repository.isBookmarked(existingQuestionId).first()
            val isBookmarkedNonExisting = repository.isBookmarked(nonExistingQuestionId).first()

            // Then
            assertTrue(isBookmarkedExisting)
            assertFalse(isBookmarkedNonExisting)
        }

        @Test
        @DisplayName("Should return bookmark folders")
        fun `getBookmarkFolders returns folders successfully`() = runTest {
            // When
            val result = repository.getBookmarkFolders()

            // Then
            assertTrue(result.isSuccess)
            val folders = result.getOrNull()
            assertNotNull(folders)
            assertEquals(2, folders!!.size)
            assertTrue(folders.any { it.name == "Android基礎" })
            assertTrue(folders.any { it.name == "Kotlin上級" })
        }

        @Test
        @DisplayName("Should create new bookmark folder")
        fun `createBookmarkFolder creates folder successfully`() = runTest {
            // Given
            val folderName = "新しいフォルダ"
            val description = "テスト用フォルダ"

            // When
            val result = repository.createBookmarkFolder(folderName, description)

            // Then
            assertTrue(result.isSuccess)
            val folder = result.getOrNull()
            assertNotNull(folder)
            assertEquals(folderName, folder!!.name)
            assertEquals(description, folder.description)
        }

        @Test
        @DisplayName("Should add bookmark to folder")
        fun `addToFolder adds bookmark to existing folder`() = runTest {
            // Given
            val bookmarkId = "bm1"
            val folderId = "folder2"

            // When
            val result = repository.addToFolder(bookmarkId, folderId)

            // Then
            assertTrue(result.isSuccess)
        }
    }

    @Nested
    @DisplayName("Learning History Tests")
    inner class LearningHistoryTests {

        @Test
        @DisplayName("Should return learning history sorted by date")
        fun `getLearningHistory returns sorted history`() = runTest {
            // When
            val history = repository.getLearningHistory().first()

            // Then
            assertEquals(3, history.size)
            // Check if sorted by answeredAt descending
            for (i in 0 until history.size - 1) {
                assertTrue(history[i].answeredAt.isAfter(history[i + 1].answeredAt))
            }
        }

        @Test
        @DisplayName("Should add learning record successfully")
        fun `addLearningRecord adds record successfully`() = runTest {
            // Given
            val newRecord = LearningHistory(
                id = "history_new",
                userId = "user1",
                quizId = "quiz_new",
                quizTitle = "New Quiz",
                questionId = "q_new",
                questionText = "New question?",
                selectedAnswer = 1,
                correctAnswer = 2,
                isCorrect = false,
                timeSpent = 30000,
                answeredAt = LocalDateTime.now(),
                category = "New Category",
                difficulty = QuizDifficulty.INTERMEDIATE
            )

            // When
            val result = repository.addLearningRecord(newRecord)
            val history = repository.getLearningHistory().first()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(4, history.size)
            assertTrue(history.any { it.id == "history_new" })
        }

        @Test
        @DisplayName("Should return quiz sessions sorted by start date")
        fun `getQuizSessions returns sorted sessions`() = runTest {
            // When
            val result = repository.getQuizSessions()

            // Then
            assertTrue(result.isSuccess)
            val sessions = result.getOrNull()
            assertNotNull(sessions)
            assertEquals(2, sessions!!.size)
            // Check if sorted by startedAt descending
            assertTrue(sessions[0].startedAt.isAfter(sessions[1].startedAt))
        }

        @Test
        @DisplayName("Should start new quiz session")
        fun `startQuizSession creates new session`() = runTest {
            // Given
            val quizId = "quiz_new"
            val quizTitle = "New Quiz Session"

            // When
            val result = repository.startQuizSession(quizId, quizTitle)

            // Then
            assertTrue(result.isSuccess)
            val session = result.getOrNull()
            assertNotNull(session)
            assertEquals(quizId, session!!.quizId)
            assertEquals(quizTitle, session.quizTitle)
            assertEquals(QuizSessionStatus.IN_PROGRESS, session.status)
        }

        @Test
        @DisplayName("Should complete quiz session successfully")
        fun `completeQuizSession updates session correctly`() = runTest {
            // Given
            val sessionId = "session2"
            val answers = listOf(
                SessionAnswer(
                    questionId = "q1",
                    selectedAnswer = 1,
                    correctAnswer = 1,
                    isCorrect = true,
                    timeSpent = 30000,
                    answeredAt = LocalDateTime.now()
                ),
                SessionAnswer(
                    questionId = "q2",
                    selectedAnswer = 2,
                    correctAnswer = 1,
                    isCorrect = false,
                    timeSpent = 45000,
                    answeredAt = LocalDateTime.now()
                )
            )

            // When
            val result = repository.completeQuizSession(sessionId, answers)

            // Then
            assertTrue(result.isSuccess)
            val session = result.getOrNull()
            assertNotNull(session)
            assertEquals(QuizSessionStatus.COMPLETED, session!!.status)
            assertEquals(2, session.answeredQuestions)
            assertEquals(1, session.correctAnswers)
            assertEquals(50.0f, session.scorePercentage)
            assertNotNull(session.completedAt)
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    inner class StatisticsTests {

        @Test
        @DisplayName("Should return comprehensive learning statistics")
        fun `getLearningStatistics returns complete stats`() = runTest {
            // When
            val result = repository.getLearningStatistics()

            // Then
            assertTrue(result.isSuccess)
            val stats = result.getOrNull()
            assertNotNull(stats)
            
            with(stats!!) {
                assertEquals("user1", userId)
                assertTrue(totalQuestionsAnswered > 0)
                assertTrue(totalCorrectAnswers >= 0)
                assertTrue(overallAccuracy >= 0.0f && overallAccuracy <= 1.0f)
                assertTrue(categoryStats.isNotEmpty())
                assertTrue(difficultyStats.isNotEmpty())
                assertTrue(monthlyProgress.isNotEmpty())
                assertTrue(achievements.isNotEmpty())
            }
        }

        @Test
        @DisplayName("Should return category statistics")
        fun `getCategoryStatistics returns category breakdown`() = runTest {
            // When
            val result = repository.getCategoryStatistics()

            // Then
            assertTrue(result.isSuccess)
            val categoryStats = result.getOrNull()
            assertNotNull(categoryStats)
            assertTrue(categoryStats!!.isNotEmpty())
            
            categoryStats.values.forEach { stat ->
                assertTrue(stat.questionsAnswered >= 0)
                assertTrue(stat.correctAnswers >= 0)
                assertTrue(stat.accuracy >= 0.0f && stat.accuracy <= 1.0f)
                assertTrue(stat.averageTimePerQuestion > 0)
            }
        }

        @Test
        @DisplayName("Should return difficulty statistics")
        fun `getDifficultyStatistics returns difficulty breakdown`() = runTest {
            // When
            val result = repository.getDifficultyStatistics()

            // Then
            assertTrue(result.isSuccess)
            val difficultyStats = result.getOrNull()
            assertNotNull(difficultyStats)
            assertTrue(difficultyStats!!.isNotEmpty())
            
            difficultyStats.values.forEach { stat ->
                assertTrue(QuizDifficulty.values().contains(stat.difficulty))
                assertTrue(stat.questionsAnswered >= 0)
                assertTrue(stat.correctAnswers >= 0)
                assertTrue(stat.accuracy >= 0.0f && stat.accuracy <= 1.0f)
            }
        }

        @Test
        @DisplayName("Should return monthly progress")
        fun `getMonthlyProgress returns progress data`() = runTest {
            // When
            val result = repository.getMonthlyProgress()

            // Then
            assertTrue(result.isSuccess)
            val monthlyProgress = result.getOrNull()
            assertNotNull(monthlyProgress)
            assertEquals(2, monthlyProgress!!.size)
            
            monthlyProgress.forEach { progress ->
                assertTrue(progress.month.matches(Regex("\\d{4}-\\d{2}")))
                assertTrue(progress.questionsAnswered >= 0)
                assertTrue(progress.averageScore >= 0.0f && progress.averageScore <= 100.0f)
                assertTrue(progress.studyDays >= 0)
            }
        }

        @Test
        @DisplayName("Should return weekly progress")
        fun `getWeeklyProgress returns weekly data`() = runTest {
            // When
            val result = repository.getWeeklyProgress()

            // Then
            assertTrue(result.isSuccess)
            val weeklyProgress = result.getOrNull()
            assertNotNull(weeklyProgress)
            assertTrue(weeklyProgress!!.isNotEmpty())
            
            weeklyProgress.forEach { progress ->
                assertNotNull(progress.weekStart)
                assertTrue(progress.dailyStats.isNotEmpty())
                assertNotNull(progress.weeklyTotal)
            }
        }

        @Test
        @DisplayName("Should return daily statistics")
        fun `getDailyStats returns daily data`() = runTest {
            // Given
            val date = "2025-06-09"

            // When
            val result = repository.getDailyStats(date)

            // Then
            assertTrue(result.isSuccess)
            val dailyStats = result.getOrNull()
            assertNotNull(dailyStats)
            
            with(dailyStats!!) {
                assertTrue(questionsAnswered >= 0)
                assertTrue(correctAnswers >= 0)
                assertTrue(averageScore >= 0.0f && averageScore <= 100.0f)
                assertTrue(studyStreak >= 0)
            }
        }
    }

    @Nested
    @DisplayName("Achievement Tests")
    inner class AchievementTests {

        @Test
        @DisplayName("Should return achievements list")
        fun `getAchievements returns achievements`() = runTest {
            // When
            val result = repository.getAchievements()

            // Then
            assertTrue(result.isSuccess)
            val achievements = result.getOrNull()
            assertNotNull(achievements)
            assertEquals(4, achievements!!.size)
            
            // Check achievement properties
            achievements.forEach { achievement ->
                assertNotNull(achievement.id)
                assertNotNull(achievement.title)
                assertNotNull(achievement.description)
                assertTrue(AchievementType.values().contains(achievement.type))
                assertTrue(achievement.progress >= 0)
                assertTrue(achievement.maxProgress > 0)
            }
        }

        @Test
        @DisplayName("Should unlock achievement successfully")
        fun `unlockAchievement processes successfully`() = runTest {
            // Given
            val achievementId = "ach3"

            // When
            val result = repository.unlockAchievement(achievementId)

            // Then
            assertTrue(result.isSuccess)
        }

        @Test
        @DisplayName("Should return performance analysis")
        fun `getPerformanceAnalysis returns analysis`() = runTest {
            // When
            val result = repository.getPerformanceAnalysis()

            // Then
            assertTrue(result.isSuccess)
            val analysis = result.getOrNull()
            assertNotNull(analysis)
            
            with(analysis!!) {
                assertTrue(strongCategories.isNotEmpty())
                assertTrue(weakCategories.isNotEmpty())
                assertTrue(recommendedStudyTime > 0)
                assertTrue(nextGoals.isNotEmpty())
                assertTrue(improvementSuggestions.isNotEmpty())
                assertNotNull(studyPattern)
            }
        }
    }

    @Nested
    @DisplayName("Search and Filter Tests")
    inner class SearchFilterTests {

        @Test
        @DisplayName("Should save search successfully")
        fun `saveSearch adds search to saved list`() = runTest {
            // Given
            val newSearch = SavedSearch(
                id = "search_new",
                name = "テスト検索",
                criteria = SearchCriteria(
                    query = "test",
                    categories = listOf("testing"),
                    difficulties = listOf(QuizDifficulty.INTERMEDIATE)
                ),
                createdAt = LocalDateTime.now(),
                lastUsed = LocalDateTime.now(),
                useCount = 1
            )

            // When
            val result = repository.saveSearch(newSearch)

            // Then
            assertTrue(result.isSuccess)
        }

        @Test
        @DisplayName("Should return saved searches sorted by last used")
        fun `getSavedSearches returns sorted searches`() = runTest {
            // When
            val result = repository.getSavedSearches()

            // Then
            assertTrue(result.isSuccess)
            val searches = result.getOrNull()
            assertNotNull(searches)
            assertEquals(2, searches!!.size)
            
            // Check if sorted by lastUsed descending
            assertTrue(searches[0].lastUsed.isAfter(searches[1].lastUsed))
        }

        @Test
        @DisplayName("Should return filter options")
        fun `getFilterOptions returns available options`() = runTest {
            // When
            val result = repository.getFilterOptions()

            // Then
            assertTrue(result.isSuccess)
            val options = result.getOrNull()
            assertNotNull(options)
            
            with(options!!) {
                assertTrue(availableCategories.isNotEmpty())
                assertTrue(availableChapters.isNotEmpty())
                assertEquals(3, difficultyRange.size)
                assertTrue(questionCountRange.first > 0)
                assertTrue(timeLimitRange.first > 0)
            }
        }
    }

    @Nested
    @DisplayName("User Preferences Tests")
    inner class UserPreferencesTests {

        @Test
        @DisplayName("Should update user preferences successfully")
        fun `updateUserPreferences processes successfully`() = runTest {
            // Given
            val preferences = mapOf(
                "theme" to "light",
                "notifications" to false,
                "dailyGoal" to 15
            )

            // When
            val result = repository.updateUserPreferences(preferences)

            // Then
            assertTrue(result.isSuccess)
        }

        @Test
        @DisplayName("Should return user preferences")
        fun `getUserPreferences returns preferences`() = runTest {
            // When
            val result = repository.getUserPreferences()

            // Then
            assertTrue(result.isSuccess)
            val preferences = result.getOrNull()
            assertNotNull(preferences)
            assertTrue(preferences!!.isNotEmpty())
            assertTrue(preferences.containsKey("theme"))
            assertTrue(preferences.containsKey("notifications"))
            assertTrue(preferences.containsKey("dailyGoal"))
        }
    }

    @Nested
    @DisplayName("Data Consistency Tests")
    inner class DataConsistencyTests {

        @Test
        @DisplayName("Should maintain data consistency across operations")
        fun `data operations maintain consistency`() = runTest {
            // Given
            val initialBookmarks = repository.getBookmarks().first()
            val initialCount = initialBookmarks.size

            // When - Add and then remove a bookmark
            repository.addBookmark("q_test", "quiz_test", "test note")
            val afterAdd = repository.getBookmarks().first()
            repository.removeBookmark("q_test")
            val afterRemove = repository.getBookmarks().first()

            // Then
            assertEquals(initialCount + 1, afterAdd.size)
            assertEquals(initialCount, afterRemove.size)
        }

        @Test
        @DisplayName("Should handle edge cases gracefully")
        fun `handles edge cases without errors`() = runTest {
            // Test removing non-existent bookmark
            val result1 = repository.removeBookmark("non_existent_question")
            assertTrue(result1.isSuccess)

            // Test completing non-existent session
            val result2 = repository.completeQuizSession("non_existent_session", emptyList())
            assertTrue(result2.isFailure)

            // Test bookmark check for non-existent question
            val isBookmarked = repository.isBookmarked("non_existent_question").first()
            assertFalse(isBookmarked)
        }
    }
}