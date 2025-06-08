package com.example.quiz_app.integration

import app.cash.turbine.test
import com.example.quiz_app.data.repository.FakeQuizRepository
import com.example.quiz_app.presentation.quiz_list.QuizListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizE2EIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `E2E test - Repository to ViewModel to UI state flow`() = runTest {
        // Given: Real repository implementation with fake data
        val fakeRepository = FakeQuizRepository()
        
        // When: ViewModel is created and initialized
        val viewModel = QuizListViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: UI state should contain data from repository
        viewModel.uiState.test {
            val state = awaitItem()
            
            // Verify loading is complete
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
            
            // Verify data from FakeRepository is present
            assertEquals(4, state.quizzes.size)
            
            // Verify specific quiz data mapping
            val firstQuiz = state.quizzes.first()
            assertEquals("1", firstQuiz.id)
            assertEquals("Android Development Fundamentals", firstQuiz.title)
            assertTrue(firstQuiz.description.contains("Android development concepts"))
            assertEquals("android", firstQuiz.categoryId)
            assertEquals("Android Development", firstQuiz.categoryName)
            assertEquals(15, firstQuiz.questionCount)
            assertEquals(20, firstQuiz.timeLimit)
            
            // Verify all difficulty levels are represented
            val difficulties = state.quizzes.map { it.difficulty }.toSet()
            assertEquals(3, difficulties.size) // BEGINNER, INTERMEDIATE, ADVANCED
        }
    }

    @Test
    fun `E2E test - Repository error propagates to UI state`() = runTest {
        // Given: Repository that will throw an error
        val errorRepository = object : com.example.quiz_app.domain.repository.QuizRepository {
            override fun getQuizzes() = kotlinx.coroutines.flow.flow<List<com.example.quiz_app.domain.Quiz>> {
                throw RuntimeException("Network connection failed")
            }
            override fun getQuizById(id: String) = kotlinx.coroutines.flow.flowOf<com.example.quiz_app.domain.Quiz?>(null)
        }
        
        // When: ViewModel is created with error repository
        val viewModel = QuizListViewModel(errorRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then: Error state should be properly handled
        viewModel.uiState.test {
            val state = awaitItem()
            
            assertFalse(state.isLoading)
            assertTrue(state.quizzes.isEmpty())
            assertEquals("Network connection failed", state.errorMessage)
        }
    }

    @Test
    fun `E2E test - Retry functionality works end-to-end`() = runTest {
        // Given: Repository that fails first, succeeds second
        var callCount = 0
        val flakyRepository = object : com.example.quiz_app.domain.repository.QuizRepository {
            override fun getQuizzes() = kotlinx.coroutines.flow.flow<List<com.example.quiz_app.domain.Quiz>> {
                callCount++
                if (callCount == 1) {
                    throw RuntimeException("First attempt failed")
                } else {
                    // Get the fake data and emit it
                    val fakeRepo = FakeQuizRepository()
                    val quizzes = fakeRepo.getQuizzes().first()
                    emit(quizzes)
                }
            }
            override fun getQuizById(id: String) = kotlinx.coroutines.flow.flowOf<com.example.quiz_app.domain.Quiz?>(null)
        }
        
        // When: ViewModel is created (first call fails)
        val viewModel = QuizListViewModel(flakyRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verify error state first
        val errorState = viewModel.uiState.value
        assertNotNull(errorState.errorMessage)
        assertEquals("First attempt failed", errorState.errorMessage)
        
        // When: Retry is called
        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verify success state after retry
        val successState = viewModel.uiState.value
        assertFalse(successState.isLoading)
        assertNull(successState.errorMessage)
        assertEquals(4, successState.quizzes.size)
        
        // Verify retry was actually called
        assertEquals(2, callCount)
    }

    @Test
    fun `E2E test - UI state transitions work correctly`() = runTest {
        // This test verifies the complete state transition flow
        // that would occur in the actual UI
        
        val fakeRepository = FakeQuizRepository()
        val viewModel = QuizListViewModel(fakeRepository)
        
        // Allow initialization to complete
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verify final state after initialization
        viewModel.uiState.test {
            val finalState = awaitItem()
            
            // Should be in success state after initialization
            assertFalse(finalState.isLoading)
            assertEquals(4, finalState.quizzes.size)
            assertNull(finalState.errorMessage)
            
            // Verify data integrity
            val firstQuiz = finalState.quizzes.first()
            assertEquals("1", firstQuiz.id)
            assertEquals("Android Development Fundamentals", firstQuiz.title)
        }
    }
}