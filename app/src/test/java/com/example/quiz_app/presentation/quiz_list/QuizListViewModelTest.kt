package com.example.quiz_app.presentation.quiz_list

import app.cash.turbine.test
import com.example.quiz_app.data.repository.FakeQuizRepository
import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import com.example.quiz_app.domain.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class QuizListViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: QuizRepository
    private lateinit var viewModel: QuizListViewModel
    
    private val sampleQuizzes = listOf(
        Quiz(
            id = "1",
            title = "Android Basics",
            description = "Basic Android concepts",
            difficulty = QuizDifficulty.BEGINNER,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 10,
            timeLimit = 15,
            imageUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Quiz(
            id = "2",
            title = "Kotlin Advanced",
            description = "Advanced Kotlin features",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 20,
            timeLimit = 30,
            imageUrl = null,
            createdAt = "2024-01-02T00:00:00Z",
            updatedAt = "2024-01-02T00:00:00Z"
        )
    )
    
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
    }
    
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `when ViewModel is initialized, it should emit loading state then success state with quizzes`() = runTest {
        // Given
        whenever(mockRepository.getQuizzes()).thenReturn(flowOf(sampleQuizzes))
        
        // When
        viewModel = QuizListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            // Should have success state with quizzes
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(sampleQuizzes, state.quizzes)
            assertNull(state.errorMessage)
        }
    }
    
    @Test
    fun `when repository throws exception, it should emit error state`() = runTest {
        // Given
        val errorMessage = "Network error"
        val errorFlow = flow<List<Quiz>> { 
            throw RuntimeException(errorMessage) 
        }
        whenever(mockRepository.getQuizzes()).thenReturn(errorFlow)
        
        // When
        viewModel = QuizListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.quizzes.isEmpty())
            assertEquals(errorMessage, state.errorMessage)
        }
    }
    
    @Test
    fun `when retry is called after error, it should reload quizzes`() = runTest {
        // Given
        val errorFlow = flow<List<Quiz>> { 
            throw RuntimeException("Network error") 
        }
        val successFlow = flowOf(sampleQuizzes)
        
        whenever(mockRepository.getQuizzes())
            .thenReturn(errorFlow)
            .thenReturn(successFlow)
        
        viewModel = QuizListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - call retry
        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(sampleQuizzes, state.quizzes)
            assertNull(state.errorMessage)
        }
    }
    
    @Test
    fun `when repository returns empty list, it should emit success state with empty list`() = runTest {
        // Given
        whenever(mockRepository.getQuizzes()).thenReturn(flowOf(emptyList()))
        
        // When
        viewModel = QuizListViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.quizzes.isEmpty())
            assertNull(state.errorMessage)
        }
    }
    
    @Test
    fun `test with FakeQuizRepository integration`() = runTest {
        // Given
        val fakeRepository = FakeQuizRepository()
        
        // When
        viewModel = QuizListViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(54, state.quizzes.size) // FakeRepository has 54 quizzes
            assertNull(state.errorMessage)
            
            // Verify first quiz data
            val firstQuiz = state.quizzes.first()
            assertEquals("1", firstQuiz.id)
            assertEquals("Android Development Fundamentals", firstQuiz.title)
            assertEquals(QuizDifficulty.BEGINNER, firstQuiz.difficulty)
        }
    }
}