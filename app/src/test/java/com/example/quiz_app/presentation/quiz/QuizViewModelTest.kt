package com.example.quiz_app.presentation.quiz

import com.example.quiz_app.domain.Question
import com.example.quiz_app.domain.repository.QuizRepository
import com.example.quiz_app.domain.repository.UserRepository
import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import com.example.quiz_app.domain.Bookmark
import com.example.quiz_app.domain.LearningHistory
import com.example.quiz_app.domain.*
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
class QuizViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope
    private lateinit var mockRepository: QuizRepository
    private lateinit var mockUserRepository: UserRepository
    private lateinit var viewModel: QuizViewModel

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
        
        mockRepository = object : QuizRepository {
            override fun getQuizzes(): Flow<List<Quiz>> = flowOf(emptyList())
            override suspend fun getQuizById(id: String): Result<Quiz> = Result.success(Quiz(
                id = "1", title = "Test Quiz", description = "Test", difficulty = QuizDifficulty.BEGINNER,
                categoryId = "test", categoryName = "Test", questionCount = 2, timeLimit = 10,
                imageUrl = null, createdAt = "", updatedAt = ""
            ))
            override suspend fun searchQuizzes(query: String): Result<List<Quiz>> = Result.success(emptyList())
            override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> = Result.success(emptyList())
            override suspend fun getQuizDetail(quizId: String): Result<List<Question>> = Result.success(sampleQuestions)
        }
        
        mockUserRepository = object : UserRepository {
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
        
        viewModel = QuizViewModel(mockRepository, mockUserRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadQuiz sets loading state initially`() = testScope.runTest {
        // When: Loading a quiz
        viewModel.loadQuiz("1")
        
        // Then: Should be in loading state initially
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadQuiz loads questions successfully`() = testScope.runTest {
        // When: Loading a quiz
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Questions should be loaded
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals(2, uiState.questions.size)
        assertEquals(2, uiState.totalQuestions)
        assertEquals(0, uiState.currentQuestionIndex)
        assertEquals("What is 2 + 2?", uiState.questions[0].questionText)
    }

    @Test
    fun `loadQuiz handles repository error`() = testScope.runTest {
        // Given: Repository that returns error
        val errorRepository = object : QuizRepository {
            override fun getQuizzes(): Flow<List<Quiz>> = flowOf(emptyList())
            override suspend fun getQuizById(id: String): Result<Quiz> = Result.failure(RuntimeException("Network error"))
            override suspend fun searchQuizzes(query: String): Result<List<Quiz>> = Result.success(emptyList())
            override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> = Result.success(emptyList())
            override suspend fun getQuizDetail(quizId: String): Result<List<Question>> = Result.failure(RuntimeException("Quiz not found"))
        }
        
        val errorViewModel = QuizViewModel(errorRepository, mockUserRepository)

        // When: Loading a quiz that fails
        errorViewModel.loadQuiz("invalid")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Error state should be set
        val uiState = errorViewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNotNull(uiState.errorMessage)
        assertTrue(uiState.errorMessage!!.contains("Quiz not found"))
    }

    @Test
    fun `selectAnswer updates selected answer index`() = testScope.runTest {
        // Given: Quiz is loaded
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Selecting an answer
        viewModel.selectAnswer(2)

        // Then: Selected answer index should be updated
        assertEquals(2, viewModel.uiState.value.selectedAnswerIndex)
    }

    @Test
    fun `submitAnswer with correct answer updates score and shows result`() = testScope.runTest {
        // Given: Quiz is loaded and answer is selected
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectAnswer(1) // Correct answer

        // When: Submitting the answer
        viewModel.submitAnswer()

        // Then: Should show correct result and update score
        val uiState = viewModel.uiState.value
        assertTrue(uiState.showResult)
        assertEquals(true, uiState.isAnswerCorrect)
        assertEquals(1, uiState.score)
    }

    @Test
    fun `submitAnswer with incorrect answer shows result but doesn't update score`() = testScope.runTest {
        // Given: Quiz is loaded and wrong answer is selected
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectAnswer(0) // Incorrect answer

        // When: Submitting the answer
        viewModel.submitAnswer()

        // Then: Should show incorrect result and not update score
        val uiState = viewModel.uiState.value
        assertTrue(uiState.showResult)
        assertEquals(false, uiState.isAnswerCorrect)
        assertEquals(0, uiState.score)
    }

    @Test
    fun `submitAnswer with no selection does nothing`() = testScope.runTest {
        // Given: Quiz is loaded but no answer selected
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Submitting without selection
        viewModel.submitAnswer()

        // Then: Should not show result
        val uiState = viewModel.uiState.value
        assertFalse(uiState.showResult)
        assertNull(uiState.isAnswerCorrect)
    }

    @Test
    fun `nextQuestion moves to next question`() = testScope.runTest {
        // Given: Quiz is loaded, answer submitted
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectAnswer(1)
        viewModel.submitAnswer()

        // When: Moving to next question
        viewModel.nextQuestion()

        // Then: Should move to next question and reset state
        val uiState = viewModel.uiState.value
        assertEquals(1, uiState.currentQuestionIndex)
        assertNull(uiState.selectedAnswerIndex)
        assertFalse(uiState.showResult)
        assertNull(uiState.isAnswerCorrect)
        assertEquals("What is the capital of Japan?", uiState.questions[1].questionText)
    }

    @Test
    fun `nextQuestion on last question finishes quiz`() = testScope.runTest {
        // Given: Quiz is loaded and on the last question
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Answer first question
        viewModel.selectAnswer(1)
        viewModel.submitAnswer()
        viewModel.nextQuestion()
        
        // Answer second question
        viewModel.selectAnswer(1)
        viewModel.submitAnswer()

        // When: Moving to next after last question
        viewModel.nextQuestion()

        // Then: Quiz should be finished
        val uiState = viewModel.uiState.value
        assertTrue(uiState.isQuizFinished)
    }

    @Test
    fun `resetQuiz resets all state to initial values`() = testScope.runTest {
        // Given: Quiz is in progress with some state
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectAnswer(1)
        viewModel.submitAnswer()

        // When: Resetting the quiz
        viewModel.resetQuiz()

        // Then: All state should be reset
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.questions.isEmpty())
        assertEquals(0, uiState.currentQuestionIndex)
        assertEquals(0, uiState.totalQuestions)
        assertNull(uiState.selectedAnswerIndex)
        assertEquals(0, uiState.score)
        assertFalse(uiState.showResult)
        assertNull(uiState.isAnswerCorrect)
        assertFalse(uiState.isQuizFinished)
        assertNull(uiState.errorMessage)
    }

    @Test
    fun `getCurrentQuestion returns correct question`() = testScope.runTest {
        // Given: Quiz is loaded
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Getting current question
        val currentQuestion = viewModel.getCurrentQuestion()

        // Then: Should return first question
        assertNotNull(currentQuestion)
        assertEquals("What is 2 + 2?", currentQuestion!!.questionText)
        assertEquals("q1", currentQuestion.id)

        // When: Moving to next question
        viewModel.selectAnswer(1)
        viewModel.submitAnswer()
        viewModel.nextQuestion()

        // Then: Should return second question
        val nextQuestion = viewModel.getCurrentQuestion()
        assertNotNull(nextQuestion)
        assertEquals("What is the capital of Japan?", nextQuestion!!.questionText)
        assertEquals("q2", nextQuestion.id)
    }

    @Test
    fun `getCurrentQuestion returns null when no questions loaded`() = testScope.runTest {
        // When: Getting current question with no quiz loaded
        val currentQuestion = viewModel.getCurrentQuestion()

        // Then: Should return null
        assertNull(currentQuestion)
    }

    @Test
    fun `multiple answer selections only keep latest selection`() = testScope.runTest {
        // Given: Quiz is loaded
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Selecting multiple answers
        viewModel.selectAnswer(0)
        viewModel.selectAnswer(1)
        viewModel.selectAnswer(2)

        // Then: Only latest selection should be kept
        assertEquals(2, viewModel.uiState.value.selectedAnswerIndex)
    }

    @Test
    fun `quiz completion state is maintained correctly`() = testScope.runTest {
        // Given: Quiz with 2 questions
        viewModel.loadQuiz("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Complete first question
        viewModel.selectAnswer(1)
        viewModel.submitAnswer()
        viewModel.nextQuestion()

        // Verify not finished yet
        assertFalse(viewModel.uiState.value.isQuizFinished)

        // Complete second question
        viewModel.selectAnswer(0)
        viewModel.submitAnswer()
        viewModel.nextQuestion()

        // Then: Quiz should be finished
        assertTrue(viewModel.uiState.value.isQuizFinished)
    }
}