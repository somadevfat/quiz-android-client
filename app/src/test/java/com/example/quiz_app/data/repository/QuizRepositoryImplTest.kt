package com.example.quiz_app.data.repository

import app.cash.turbine.test
import com.example.quiz_app.data.remote.QuizApiService
import com.example.quiz_app.data.remote.dto.QuizDto
import com.example.quiz_app.domain.QuizDifficulty
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

class QuizRepositoryImplTest {

    private lateinit var apiService: QuizApiService
    private lateinit var repository: QuizRepositoryImpl

    private val sampleQuizDto = QuizDto(
        id = 1,
        qid = "Q001",
        chapter = "Android Basics",
        category = "Views",
        difficulty = "初級",
        choices = listOf("Option A", "Option B", "Option C", "Option D"),
        code = "fun example() { }",
        questionText = "What is an Activity?",
        explanation = "An Activity represents a single screen with a user interface.",
        questionCategory = "Android"
    )

    @BeforeEach
    fun setup() {
        apiService = mockk()
        repository = QuizRepositoryImpl(apiService)
    }

    @Test
    fun `getQuizzes should return mapped domain objects when API call succeeds`() = runTest {
        // Given
        val quizDtos = listOf(sampleQuizDto)
        coEvery { apiService.getQuizzes() } returns quizDtos

        // When & Then
        repository.getQuizzes().test {
            val result = awaitItem()
            awaitComplete()

            assertEquals(1, result.size)
            val quiz = result.first()
            assertEquals("1", quiz.id)
            assertEquals("Android Basics - Views", quiz.title)
            assertEquals("What is an Activity?", quiz.description)
            assertEquals(QuizDifficulty.BEGINNER, quiz.difficulty)
            assertEquals("Q001", quiz.categoryId)
            assertEquals("Views", quiz.categoryName)
            assertEquals(4, quiz.questionCount)
            assertNull(quiz.timeLimit)
            assertNull(quiz.imageUrl)
        }
    }

    @Test
    fun `getQuizzes should throw exception when API call fails`() = runTest {
        // Given
        val exception = IOException("Network error")
        coEvery { apiService.getQuizzes() } throws exception

        // When & Then
        repository.getQuizzes().test {
            val error = awaitError()
            assertTrue(error is IOException)
            assertEquals("Network error", error.message)
        }
    }

    @Test
    fun `getQuizById should return mapped domain object when API call succeeds`() = runTest {
        // Given
        coEvery { apiService.getQuizById("1") } returns sampleQuizDto

        // When & Then
        repository.getQuizById("1").test {
            val result = awaitItem()
            awaitComplete()

            assertNotNull(result)
            assertEquals("1", result!!.id)
            assertEquals("Android Basics - Views", result.title)
            assertEquals(QuizDifficulty.BEGINNER, result.difficulty)
        }
    }

    @Test
    fun `getQuizById should return null when API call fails`() = runTest {
        // Given
        coEvery { apiService.getQuizById("1") } throws IOException("Not found")

        // When & Then
        repository.getQuizById("1").test {
            val result = awaitItem()
            awaitComplete()

            assertNull(result)
        }
    }

    @Test
    fun `getQuizzes should handle empty list from API`() = runTest {
        // Given
        coEvery { apiService.getQuizzes() } returns emptyList()

        // When & Then
        repository.getQuizzes().test {
            val result = awaitItem()
            awaitComplete()

            assertTrue(result.isEmpty())
        }
    }
}