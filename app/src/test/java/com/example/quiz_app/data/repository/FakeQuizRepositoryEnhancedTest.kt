package com.example.quiz_app.data.repository

import com.example.quiz_app.domain.QuizDifficulty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

class FakeQuizRepositoryEnhancedTest {

    private lateinit var repository: FakeQuizRepository

    @BeforeEach
    fun setup() {
        repository = FakeQuizRepository()
    }

    @AfterEach
    fun tearDown() {
        // Clean up if needed
    }

    @Nested
    @DisplayName("Enhanced Quiz Data Tests")
    inner class EnhancedQuizDataTests {

        @Test
        @DisplayName("Should return 54 comprehensive quiz entries")
        fun `getQuizzes returns 54 comprehensive quiz entries`() = runTest {
            // When
            val quizzes = repository.getQuizzes().first()

            // Then
            assertEquals(54, quizzes.size)
        }

        @Test
        @DisplayName("Should have proper category distribution")
        fun `quizzes have proper category distribution`() = runTest {
            // When
            val quizzes = repository.getQuizzes().first()
            val categoryCount = quizzes.groupBy { it.categoryId }.mapValues { it.value.size }

            // Then
            assertEquals(10, categoryCount["android"]) // Android Development
            assertEquals(12, categoryCount["kotlin"]) // Kotlin Programming
            assertEquals(8, categoryCount["compose"]) // Jetpack Compose
            assertEquals(10, categoryCount["architecture"]) // Software Architecture
            assertEquals(8, categoryCount["design"]) // UI/UX Design
            assertEquals(6, categoryCount["testing"]) // Testing
        }

        @Test
        @DisplayName("Should have balanced difficulty distribution")
        fun `quizzes have balanced difficulty distribution`() = runTest {
            // When
            val quizzes = repository.getQuizzes().first()
            val difficultyCount = quizzes.groupBy { it.difficulty }.mapValues { it.value.size }

            // Then
            assertTrue(difficultyCount[QuizDifficulty.BEGINNER]!! > 0)
            assertTrue(difficultyCount[QuizDifficulty.INTERMEDIATE]!! > 0)
            assertTrue(difficultyCount[QuizDifficulty.ADVANCED]!! > 0)
            
            // Verify total adds up
            val total = difficultyCount.values.sum()
            assertEquals(54, total)
        }

        @Test
        @DisplayName("Should have realistic question counts")
        fun `quizzes have realistic question counts`() = runTest {
            // When
            val quizzes = repository.getQuizzes().first()

            // Then
            quizzes.forEach { quiz ->
                assertTrue(quiz.questionCount >= 12, "Quiz ${quiz.title} has too few questions: ${quiz.questionCount}")
                assertTrue(quiz.questionCount <= 28, "Quiz ${quiz.title} has too many questions: ${quiz.questionCount}")
            }
        }

        @Test
        @DisplayName("Should have appropriate time limits")
        fun `quizzes have appropriate time limits`() = runTest {
            // When
            val quizzes = repository.getQuizzes().first()

            // Then
            quizzes.forEach { quiz ->
                if (quiz.timeLimit != null) {
                    assertTrue(quiz.timeLimit!! >= 15, "Quiz ${quiz.title} has too short time limit: ${quiz.timeLimit}")
                    assertTrue(quiz.timeLimit!! <= 45, "Quiz ${quiz.title} has too long time limit: ${quiz.timeLimit}")
                }
            }
        }
    }

    @Nested
    @DisplayName("Category-based Filtering Tests")
    inner class CategoryFilteringTests {

        @Test
        @DisplayName("Should filter Android Development quizzes correctly")
        fun `getQuizzesByCategory filters Android quizzes correctly`() = runTest {
            // When
            val result = repository.getQuizzesByCategory("android")

            // Then
            assertTrue(result.isSuccess)
            val androidQuizzes = result.getOrNull()!!
            assertEquals(10, androidQuizzes.size)
            androidQuizzes.forEach { quiz ->
                assertEquals("android", quiz.categoryId)
                assertEquals("Android Development", quiz.categoryName)
            }
        }

        @Test
        @DisplayName("Should filter Kotlin Programming quizzes correctly")
        fun `getQuizzesByCategory filters Kotlin quizzes correctly`() = runTest {
            // When
            val result = repository.getQuizzesByCategory("kotlin")

            // Then
            assertTrue(result.isSuccess)
            val kotlinQuizzes = result.getOrNull()!!
            assertEquals(12, kotlinQuizzes.size)
            kotlinQuizzes.forEach { quiz ->
                assertEquals("kotlin", quiz.categoryId)
                assertEquals("Kotlin Programming", quiz.categoryName)
            }
        }

        @Test
        @DisplayName("Should filter Jetpack Compose quizzes correctly")
        fun `getQuizzesByCategory filters Compose quizzes correctly`() = runTest {
            // When
            val result = repository.getQuizzesByCategory("compose")

            // Then
            assertTrue(result.isSuccess)
            val composeQuizzes = result.getOrNull()!!
            assertEquals(8, composeQuizzes.size)
            composeQuizzes.forEach { quiz ->
                assertEquals("compose", quiz.categoryId)
                assertEquals("Jetpack Compose", quiz.categoryName)
            }
        }

        @Test
        @DisplayName("Should return empty list for non-existent category")
        fun `getQuizzesByCategory returns empty for non-existent category`() = runTest {
            // When
            val result = repository.getQuizzesByCategory("non_existent")

            // Then
            assertTrue(result.isSuccess)
            val quizzes = result.getOrNull()!!
            assertTrue(quizzes.isEmpty())
        }
    }

    @Nested
    @DisplayName("Search Functionality Tests")
    inner class SearchFunctionalityTests {

        @Test
        @DisplayName("Should search by title successfully")
        fun `searchQuizzes finds quizzes by title`() = runTest {
            // When
            val result = repository.searchQuizzes("Android")

            // Then
            assertTrue(result.isSuccess)
            val foundQuizzes = result.getOrNull()!!
            assertTrue(foundQuizzes.isNotEmpty())
            foundQuizzes.forEach { quiz ->
                assertTrue(quiz.title.contains("Android", ignoreCase = true))
            }
        }

        @Test
        @DisplayName("Should search case-insensitively")
        fun `searchQuizzes is case insensitive`() = runTest {
            // When
            val result1 = repository.searchQuizzes("kotlin")
            val result2 = repository.searchQuizzes("KOTLIN")
            val result3 = repository.searchQuizzes("Kotlin")

            // Then
            assertTrue(result1.isSuccess)
            assertTrue(result2.isSuccess)
            assertTrue(result3.isSuccess)
            
            val quizzes1 = result1.getOrNull()!!
            val quizzes2 = result2.getOrNull()!!
            val quizzes3 = result3.getOrNull()!!
            
            assertEquals(quizzes1.size, quizzes2.size)
            assertEquals(quizzes2.size, quizzes3.size)
        }

        @Test
        @DisplayName("Should return empty list for non-matching search")
        fun `searchQuizzes returns empty for non-matching query`() = runTest {
            // When
            val result = repository.searchQuizzes("XYZ123NonExistent")

            // Then
            assertTrue(result.isSuccess)
            val foundQuizzes = result.getOrNull()!!
            assertTrue(foundQuizzes.isEmpty())
        }

        @Test
        @DisplayName("Should find partial matches")
        fun `searchQuizzes finds partial matches`() = runTest {
            // When
            val result = repository.searchQuizzes("Compose")

            // Then
            assertTrue(result.isSuccess)
            val foundQuizzes = result.getOrNull()!!
            assertTrue(foundQuizzes.isNotEmpty())
            foundQuizzes.forEach { quiz ->
                assertTrue(quiz.title.contains("Compose", ignoreCase = true))
            }
        }
    }

    @Nested
    @DisplayName("Quiz Detail Tests")
    inner class QuizDetailTests {

        @Test
        @DisplayName("Should return Android Fundamentals questions")
        fun `getQuizDetail returns Android Fundamentals questions`() = runTest {
            // When
            val result = repository.getQuizDetail("1")

            // Then
            assertTrue(result.isSuccess)
            val questions = result.getOrNull()!!
            assertEquals(5, questions.size)
            
            // Verify question structure
            questions.forEach { question ->
                assertNotNull(question.id)
                assertNotNull(question.questionText)
                assertEquals(4, question.options.size)
                assertTrue(question.correctAnswerIndex >= 0 && question.correctAnswerIndex < 4)
                assertNotNull(question.explanation)
            }
        }

        @Test
        @DisplayName("Should return Kotlin Basics questions")
        fun `getQuizDetail returns Kotlin Basics questions`() = runTest {
            // When
            val result = repository.getQuizDetail("11")

            // Then
            assertTrue(result.isSuccess)
            val questions = result.getOrNull()!!
            assertEquals(3, questions.size)
            
            // Verify Kotlin-specific content
            val kotlinQuestion = questions.find { it.questionText.contains("Kotlin") }
            assertNotNull(kotlinQuestion)
        }

        @Test
        @DisplayName("Should return Compose Basics questions")
        fun `getQuizDetail returns Compose Basics questions`() = runTest {
            // When
            val result = repository.getQuizDetail("23")

            // Then
            assertTrue(result.isSuccess)
            val questions = result.getOrNull()!!
            assertEquals(2, questions.size)
            
            // Verify Compose-specific content
            val composeQuestion = questions.find { it.questionText.contains("Compose") }
            assertNotNull(composeQuestion)
        }

        @Test
        @DisplayName("Should return default questions for unknown quiz")
        fun `getQuizDetail returns default questions for unknown quiz`() = runTest {
            // When
            val result = repository.getQuizDetail("999")

            // Then
            assertTrue(result.isSuccess)
            val questions = result.getOrNull()!!
            assertEquals(3, questions.size)
            
            // Verify default question structure
            questions.forEach { question ->
                assertTrue(question.id.startsWith("q_999_"))
                assertEquals(4, question.options.size)
            }
        }
    }

    @Nested
    @DisplayName("Individual Quiz Access Tests")
    inner class IndividualQuizAccessTests {

        @Test
        @DisplayName("Should get specific quiz by ID")
        fun `getQuizById returns specific quiz successfully`() = runTest {
            // When
            val result = repository.getQuizById("1")

            // Then
            assertTrue(result.isSuccess)
            val quiz = result.getOrNull()!!
            assertEquals("1", quiz.id)
            assertEquals("Android Development Fundamentals", quiz.title)
            assertEquals(QuizDifficulty.BEGINNER, quiz.difficulty)
        }

        @Test
        @DisplayName("Should handle non-existent quiz ID")
        fun `getQuizById handles non-existent ID gracefully`() = runTest {
            // When
            val result = repository.getQuizById("999")

            // Then
            assertTrue(result.isFailure)
            assertEquals("Not found", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("Should get Kotlin Advanced quiz correctly")
        fun `getQuizById returns Kotlin Advanced quiz correctly`() = runTest {
            // When
            val result = repository.getQuizById("12")

            // Then
            assertTrue(result.isSuccess)
            val quiz = result.getOrNull()!!
            assertEquals("12", quiz.id)
            assertEquals("Kotlin Advanced Features", quiz.title)
            assertEquals("kotlin", quiz.categoryId)
            assertEquals(QuizDifficulty.ADVANCED, quiz.difficulty)
        }

        @Test
        @DisplayName("Should get Architecture quiz correctly")
        fun `getQuizById returns Architecture quiz correctly`() = runTest {
            // When
            val result = repository.getQuizById("31")

            // Then
            assertTrue(result.isSuccess)
            val quiz = result.getOrNull()!!
            assertEquals("31", quiz.id)
            assertEquals("Clean Architecture Patterns", quiz.title)
            assertEquals("architecture", quiz.categoryId)
            assertEquals(QuizDifficulty.ADVANCED, quiz.difficulty)
        }
    }

    @Nested
    @DisplayName("Data Quality and Consistency Tests")
    inner class DataQualityTests {

        @Test
        @DisplayName("Should have unique quiz IDs")
        fun `all quizzes have unique IDs`() = runTest {
            // When
            val quizzes = repository.getQuizzes().first()
            val uniqueIds = quizzes.map { it.id }.toSet()

            // Then
            assertEquals(quizzes.size, uniqueIds.size)
        }

        @Test
        @DisplayName("Should have valid creation dates")
        fun `all quizzes have valid creation dates`() = runTest {
            // When
            val quizzes = repository.getQuizzes().first()

            // Then
            quizzes.forEach { quiz ->
                assertNotNull(quiz.createdAt)
                assertNotNull(quiz.updatedAt)
                assertTrue(quiz.createdAt.isNotEmpty())
                assertTrue(quiz.updatedAt.isNotEmpty())
            }
        }

        @Test
        @DisplayName("Should have consistent category data")
        fun `quizzes have consistent category data`() = runTest {
            // When
            val quizzes = repository.getQuizzes().first()

            // Then
            quizzes.forEach { quiz ->
                assertNotNull(quiz.categoryId)
                assertNotNull(quiz.categoryName)
                assertTrue(quiz.categoryId.isNotEmpty())
                assertTrue(quiz.categoryName.isNotEmpty())
            }
        }

        @Test
        @DisplayName("Should have realistic image URLs")
        fun `quizzes have realistic image URLs`() = runTest {
            // When
            val quizzes = repository.getQuizzes().first()

            // Then
            quizzes.forEach { quiz ->
                assertNotNull(quiz.imageUrl)
                assertTrue(quiz.imageUrl!!.startsWith("https://example.com/"))
                assertTrue(quiz.imageUrl!!.endsWith(".jpg"))
            }
        }
    }

    @Nested
    @DisplayName("Performance and Efficiency Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should handle multiple concurrent requests efficiently")
        fun `handles multiple concurrent requests`() = runTest {
            // When - Multiple concurrent operations
            val quizzesFlow = repository.getQuizzes()
            val searchResult = repository.searchQuizzes("Android")
            val categoryResult = repository.getQuizzesByCategory("kotlin")
            val quizResult = repository.getQuizById("1")

            // Then - All operations should complete successfully
            val quizzes = quizzesFlow.first()
            assertTrue(quizzes.isNotEmpty())
            assertTrue(searchResult.isSuccess)
            assertTrue(categoryResult.isSuccess)
            assertTrue(quizResult.isSuccess)
        }

        @Test
        @DisplayName("Should provide consistent data across calls")
        fun `provides consistent data across multiple calls`() = runTest {
            // When - Multiple calls to same method
            val quizzes1 = repository.getQuizzes().first()
            val quizzes2 = repository.getQuizzes().first()
            val quizzes3 = repository.getQuizzes().first()

            // Then - Results should be identical
            assertEquals(quizzes1.size, quizzes2.size)
            assertEquals(quizzes2.size, quizzes3.size)
            assertEquals(quizzes1.map { it.id }, quizzes2.map { it.id })
            assertEquals(quizzes2.map { it.id }, quizzes3.map { it.id })
        }
    }
}