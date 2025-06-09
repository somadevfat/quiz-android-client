package com.example.quiz_app.data.repository

import com.example.quiz_app.data.remote.QuizApiService
import com.example.quiz_app.data.remote.dto.QuizDto
import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class QuizRepositoryImplTest {
    
    private lateinit var apiService: QuizApiService
    private lateinit var repository: QuizRepositoryImpl
    
    private val sampleQuizDto = QuizDto(
        id = 1,
        qid = "android-001",
        chapter = "Android Basics",
        category = "Views",
        difficulty = "初級",
        choices = listOf("Option 1", "Option 2", "Option 3", "Option 4"),
        code = null,
        questionText = "What is a View in Android?",
        explanation = "A View is a basic building block for UI components.",
        questionCategory = "Android Development"
    )
    
    @BeforeEach
    fun setup() {
        apiService = mockk()
        repository = QuizRepositoryImpl(apiService)
    }
    
    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }
    
    @Test
    fun `getQuizzes returns list of quizzes from API`() = runTest {
        // Given
        coEvery { apiService.getQuizzes() } returns listOf(sampleQuizDto)
        
        // When
        val result = repository.getQuizzes().first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals("Android Basics - Views", result[0].title)
        assertEquals(QuizDifficulty.BEGINNER, result[0].difficulty)
    }
    
    @Test
    fun `getQuizById returns specific quiz successfully`() = runTest {
        // Given
        coEvery { apiService.getQuizById("1") } returns sampleQuizDto
        
        // When
        val result = repository.getQuizById("1")
        
        // Then
        assertTrue(result.isSuccess)
        val quiz = result.getOrNull()
        assertNotNull(quiz)
        assertEquals("1", quiz!!.id)
        assertEquals("Android Basics - Views", quiz.title)
        assertEquals(QuizDifficulty.BEGINNER, quiz.difficulty)
    }
    
    @Test
    fun `getQuizById handles API error gracefully`() = runTest {
        // Given
        coEvery { apiService.getQuizById("999") } throws Exception("Quiz not found")
        
        // When
        val result = repository.getQuizById("999")
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Quiz not found", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `searchQuizzes returns filtered results from getQuizzes`() = runTest {
        // Given
        val searchQuery = "Android"
        coEvery { apiService.getQuizzes() } returns listOf(sampleQuizDto)
        
        // When
        val result = repository.searchQuizzes(searchQuery)
        
        // Then
        assertTrue(result.isSuccess)
        val quizzes = result.getOrNull()
        assertNotNull(quizzes)
        assertEquals(1, quizzes!!.size)
        assertTrue(quizzes[0].title.contains("Android"))
    }
    
    @Test
    fun `getQuizzesByCategory returns category-specific quizzes from getQuizzes`() = runTest {
        // Given
        val categoryId = "android-001"
        coEvery { apiService.getQuizzes() } returns listOf(sampleQuizDto)
        
        // When
        val result = repository.getQuizzesByCategory(categoryId)
        
        // Then
        assertTrue(result.isSuccess)
        val quizzes = result.getOrNull()
        assertNotNull(quizzes)
        assertEquals(1, quizzes!!.size)
        assertEquals(categoryId, quizzes[0].categoryId)
    }
    
    @Test
    fun `getQuizDetail returns questions for quiz`() = runTest {
        // Given
        val quizId = "1"
        coEvery { apiService.getQuizById(quizId) } returns sampleQuizDto
        
        // When
        val result = repository.getQuizDetail(quizId)
        
        // Then
        assertTrue(result.isSuccess)
        val questions = result.getOrNull()
        assertNotNull(questions)
        assertEquals(1, questions!!.size)
        assertEquals("q_1", questions[0].id)
        assertEquals("What is a View in Android?", questions[0].questionText)
    }
}