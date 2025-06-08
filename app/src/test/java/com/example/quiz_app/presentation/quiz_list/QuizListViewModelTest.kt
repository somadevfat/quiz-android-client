package com.example.quiz_app.presentation.quiz_list

import app.cash.turbine.test
import com.example.quiz_app.data.repository.FakeQuizRepository
import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import com.example.quiz_app.domain.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
        
        // Then
        viewModel.uiState.test {
            // Initial state should be loading
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            assertTrue(initialState.quizzes.isEmpty())
            assertNull(initialState.errorMessage)
            
            // Then success state with quizzes
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(sampleQuizzes, successState.quizzes)
            assertNull(successState.errorMessage)
        }
    }
    
    @Test
    fun `when repository throws exception, it should emit error state`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(mockRepository.getQuizzes()).thenThrow(RuntimeException(errorMessage))
        
        // When
        viewModel = QuizListViewModel(mockRepository)
        
        // Then
        viewModel.uiState.test {
            // Initial loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            // Then error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.quizzes.isEmpty())
            assertEquals(errorMessage, errorState.errorMessage)
        }
    }
    
    @Test
    fun `when retry is called after error, it should reload quizzes`() = runTest {
        // Given
        whenever(mockRepository.getQuizzes())
            .thenThrow(RuntimeException("Network error"))
            .thenReturn(flowOf(sampleQuizzes))
        
        viewModel = QuizListViewModel(mockRepository)
        
        // When
        viewModel.uiState.test {
            // Skip initial states
            awaitItem() // loading
            awaitItem() // error
            
            // Call retry
            viewModel.retry()
            
            // Then it should emit loading and success states
            val retryLoadingState = awaitItem()
            assertTrue(retryLoadingState.isLoading)
            assertNull(retryLoadingState.errorMessage)
            
            val retrySuccessState = awaitItem()
            assertFalse(retrySuccessState.isLoading)
            assertEquals(sampleQuizzes, retrySuccessState.quizzes)
            assertNull(retrySuccessState.errorMessage)
        }
    }
    
    @Test
    fun `when repository returns empty list, it should emit success state with empty list`() = runTest {
        // Given
        whenever(mockRepository.getQuizzes()).thenReturn(flowOf(emptyList()))
        
        // When
        viewModel = QuizListViewModel(mockRepository)
        
        // Then
        viewModel.uiState.test {
            // Initial loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            // Then success state with empty list
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.quizzes.isEmpty())
            assertNull(successState.errorMessage)
        }
    }
    
    @Test
    fun `test with FakeQuizRepository integration`() = runTest {
        // Given
        val fakeRepository = FakeQuizRepository()
        
        // When
        viewModel = QuizListViewModel(fakeRepository)
        
        // Then
        viewModel.uiState.test {
            // Initial loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            // Then success state with fake data
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(4, successState.quizzes.size) // FakeRepository has 4 quizzes
            assertNull(successState.errorMessage)
            
            // Verify first quiz data
            val firstQuiz = successState.quizzes.first()
            assertEquals("1", firstQuiz.id)
            assertEquals("Android Development Fundamentals", firstQuiz.title)
            assertEquals(QuizDifficulty.BEGINNER, firstQuiz.difficulty)
        }
    }
}