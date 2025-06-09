package com.example.quiz_app.integration

import com.example.quiz_app.domain.*
import com.example.quiz_app.domain.repository.QuizRepository
import com.example.quiz_app.domain.repository.UserRepository
import com.example.quiz_app.presentation.quiz_list.QuizListViewModel
import com.example.quiz_app.presentation.quiz.QuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizE2EIntegrationTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope
    
    private val sampleQuiz = Quiz(
        id = "1",
        title = "Test Quiz",
        description = "A test quiz for integration testing",
        difficulty = QuizDifficulty.BEGINNER,
        categoryId = "test",
        categoryName = "Testing",
        questionCount = 2,
        timeLimit = 10,
        imageUrl = null,
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z"
    )
    
    private val sampleQuestions = listOf(
        Question(
            id = "q1",
            questionText = "What is 2 + 2?",
            options = listOf("3", "4", "5", "6"),
            correctAnswerIndex = 1,
            explanation = "2 + 2 equals 4"
        ),
        Question(
            id = "q2",
            questionText = "What is the capital of Japan?",
            options = listOf("Seoul", "Tokyo", "Beijing", "Bangkok"),
            correctAnswerIndex = 1,
            explanation = "Tokyo is the capital of Japan"
        )
    )

    @BeforeEach
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `E2E test - QuizListViewModel loads quizzes successfully`() = testScope.runTest {
        // Given: Repository with sample data
        val successRepository = object : QuizRepository {
            override fun getQuizzes(): Flow<List<Quiz>> = flowOf(listOf(sampleQuiz))
            override suspend fun getQuizById(id: String): Result<Quiz> = Result.success(sampleQuiz)
            override suspend fun searchQuizzes(query: String): Result<List<Quiz>> = Result.success(listOf(sampleQuiz))
            override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> = Result.success(listOf(sampleQuiz))
            override suspend fun getQuizDetail(quizId: String): Result<List<Question>> = Result.success(sampleQuestions)
        }

        // When: ViewModel is created
        val viewModel = QuizListViewModel(successRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: UI state should contain the quiz
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
        assertEquals(1, uiState.quizzes.size)
        assertEquals("Test Quiz", uiState.quizzes[0].title)
    }

    @Test
    fun `E2E test - Repository error propagates to UI state`() = testScope.runTest {
        // Given: Repository that will throw an error
        val errorRepository = object : QuizRepository {
            override fun getQuizzes(): Flow<List<Quiz>> = flowOf(emptyList())
            override suspend fun getQuizById(id: String): Result<Quiz> = Result.failure(RuntimeException("Network error"))
            override suspend fun searchQuizzes(query: String): Result<List<Quiz>> = Result.failure(RuntimeException("Network error"))
            override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> = Result.failure(RuntimeException("Network error"))
            override suspend fun getQuizDetail(quizId: String): Result<List<Question>> = Result.failure(RuntimeException("Network error"))
        }

        // Given: Mock UserRepository
        val mockUserRepository = createMockUserRepository()
        
        // When: QuizViewModel tries to load quiz
        val quizViewModel = QuizViewModel(errorRepository, mockUserRepository, testDispatcher, enableTimer = false)
        quizViewModel.loadQuiz("invalid-id")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Error state should be properly handled
        val uiState = quizViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNotNull(uiState.errorMessage)
        assertTrue(uiState.errorMessage!!.contains("Network error"))
    }

    @Test
    fun `E2E test - Complete quiz flow from start to finish`() = testScope.runTest {
        // Given: Repository with complete quiz data
        val completeRepository = object : QuizRepository {
            override fun getQuizzes(): Flow<List<Quiz>> = flowOf(listOf(sampleQuiz))
            override suspend fun getQuizById(id: String): Result<Quiz> = Result.success(sampleQuiz)
            override suspend fun searchQuizzes(query: String): Result<List<Quiz>> = Result.success(listOf(sampleQuiz))
            override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> = Result.success(listOf(sampleQuiz))
            override suspend fun getQuizDetail(quizId: String): Result<List<Question>> = Result.success(sampleQuestions)
        }

        // Given: Mock UserRepository
        val mockUserRepository = createMockUserRepository()
        
        // When: QuizViewModel loads and processes quiz
        val quizViewModel = QuizViewModel(completeRepository, mockUserRepository, testDispatcher, enableTimer = false)
        
        // Step 1: Load quiz
        quizViewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Quiz should be loaded successfully
        var uiState = quizViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals(2, uiState.questions.size)
        assertEquals(0, uiState.currentQuestionIndex)
        assertEquals("What is 2 + 2?", uiState.questions[0].questionText)

        // Step 2: Select an answer
        quizViewModel.selectAnswer(1) // Correct answer
        uiState = quizViewModel.uiState.value
        assertEquals(1, uiState.selectedAnswerIndex)

        // Step 3: Submit answer
        quizViewModel.submitAnswer()
        testDispatcher.scheduler.advanceUntilIdle()
        uiState = quizViewModel.uiState.value
        assertTrue(uiState.showResult)
        assertEquals(true, uiState.isAnswerCorrect)
        assertEquals(1, uiState.score)

        // Step 4: Move to next question
        quizViewModel.nextQuestion()
        uiState = quizViewModel.uiState.value
        assertEquals(1, uiState.currentQuestionIndex)
        assertEquals("What is the capital of Japan?", uiState.questions[1].questionText)
        assertFalse(uiState.showResult)
        assertNull(uiState.selectedAnswerIndex)

        // Step 5: Answer second question (incorrectly)
        quizViewModel.selectAnswer(0) // Incorrect answer
        quizViewModel.submitAnswer()
        testDispatcher.scheduler.advanceUntilIdle()
        uiState = quizViewModel.uiState.value
        assertTrue(uiState.showResult)
        assertEquals(false, uiState.isAnswerCorrect)
        assertEquals(1, uiState.score) // Score should remain 1

        // Step 6: Finish quiz
        quizViewModel.nextQuestion()
        uiState = quizViewModel.uiState.value
        assertTrue(uiState.isQuizFinished)
        assertEquals(1, uiState.score)
        assertEquals(2, uiState.totalQuestions)
    }

    @Test
    fun `E2E test - Quiz session state management`() = testScope.runTest {
        // Given: Repository with quiz data
        val repository = object : QuizRepository {
            override fun getQuizzes(): Flow<List<Quiz>> = flowOf(listOf(sampleQuiz))
            override suspend fun getQuizById(id: String): Result<Quiz> = Result.success(sampleQuiz)
            override suspend fun searchQuizzes(query: String): Result<List<Quiz>> = Result.success(listOf(sampleQuiz))
            override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> = Result.success(listOf(sampleQuiz))
            override suspend fun getQuizDetail(quizId: String): Result<List<Question>> = Result.success(sampleQuestions)
        }

        // Given: Mock UserRepository
        val mockUserRepository = createMockUserRepository()

        // When: QuizViewModel manages session state
        val quizViewModel = QuizViewModel(repository, mockUserRepository, testDispatcher, enableTimer = false)
        quizViewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Initial state should be correct
        var uiState = quizViewModel.uiState.value
        assertEquals(0, uiState.currentQuestionIndex)
        assertEquals(2, uiState.totalQuestions)
        assertEquals(0, uiState.score)
        assertFalse(uiState.isQuizFinished)

        // When: Reset quiz
        quizViewModel.resetQuiz()
        uiState = quizViewModel.uiState.value

        // Then: State should be reset
        assertEquals(0, uiState.currentQuestionIndex)
        assertEquals(0, uiState.totalQuestions)
        assertEquals(0, uiState.score)
        assertFalse(uiState.isQuizFinished)
        assertTrue(uiState.questions.isEmpty())
    }

    private fun createMockUserRepository(): UserRepository {
        return object : UserRepository {
            override fun getBookmarks(): Flow<List<Bookmark>> = flowOf(emptyList())
            override suspend fun addBookmark(questionId: String, quizId: String, notes: String?): Result<Unit> = Result.success(Unit)
            override suspend fun removeBookmark(questionId: String): Result<Unit> = Result.success(Unit)
            override fun isBookmarked(questionId: String): Flow<Boolean> = flowOf(false)
            override suspend fun getBookmarkFolders(): Result<List<BookmarkFolder>> = Result.failure(NotImplementedError())
            override suspend fun createBookmarkFolder(name: String, description: String?): Result<BookmarkFolder> = Result.failure(NotImplementedError())
            override suspend fun addToFolder(bookmarkId: String, folderId: String): Result<Unit> = Result.failure(NotImplementedError())
            override fun getLearningHistory(): Flow<List<LearningHistory>> = flowOf(emptyList())
            override suspend fun addLearningRecord(record: LearningHistory): Result<Unit> = Result.success(Unit)
            override suspend fun getQuizSessions(): Result<List<QuizSession>> = Result.success(emptyList())
            override suspend fun startQuizSession(quizId: String, quizTitle: String): Result<QuizSession> = Result.failure(NotImplementedError())
            override suspend fun updateQuizSession(session: QuizSession): Result<Unit> = Result.failure(NotImplementedError())
            override suspend fun completeQuizSession(sessionId: String, answers: List<SessionAnswer>): Result<QuizSession> = Result.failure(NotImplementedError())
            override suspend fun getLearningStatistics(): Result<LearningStatistics> = Result.failure(NotImplementedError())
            override suspend fun getCategoryStatistics(): Result<Map<String, CategoryStatistics>> = Result.failure(NotImplementedError())
            override suspend fun getDifficultyStatistics(): Result<Map<QuizDifficulty, DifficultyStatistics>> = Result.failure(NotImplementedError())
            override suspend fun getMonthlyProgress(): Result<List<MonthlyProgress>> = Result.failure(NotImplementedError())
            override suspend fun getWeeklyProgress(): Result<List<WeeklyProgress>> = Result.failure(NotImplementedError())
            override suspend fun getDailyStats(date: String): Result<DailyLearningStats> = Result.failure(NotImplementedError())
            override suspend fun getAchievements(): Result<List<Achievement>> = Result.failure(NotImplementedError())
            override suspend fun unlockAchievement(achievementId: String): Result<Unit> = Result.failure(NotImplementedError())
            override suspend fun getPerformanceAnalysis(): Result<PerformanceAnalysis> = Result.failure(NotImplementedError())
            override suspend fun saveSearch(search: SavedSearch): Result<Unit> = Result.failure(NotImplementedError())
            override suspend fun getSavedSearches(): Result<List<SavedSearch>> = Result.failure(NotImplementedError())
            override suspend fun getFilterOptions(): Result<FilterOptions> = Result.failure(NotImplementedError())
            override suspend fun updateUserPreferences(preferences: Map<String, Any>): Result<Unit> = Result.failure(NotImplementedError())
            override suspend fun getUserPreferences(): Result<Map<String, Any>> = Result.failure(NotImplementedError())
        }
    }
}