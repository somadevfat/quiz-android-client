package com.example.quiz_app.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class QuizTest {

    @Test
    fun `Quiz data class should hold correct values`() {
        val quiz = Quiz(
            id = "quiz-1",
            title = "Sample Quiz",
            description = "A sample quiz for testing",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "category-1",
            categoryName = "General Knowledge",
            questionCount = 10,
            timeLimit = 30,
            imageUrl = "https://example.com/image.jpg",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        assertEquals("quiz-1", quiz.id)
        assertEquals("Sample Quiz", quiz.title)
        assertEquals("A sample quiz for testing", quiz.description)
        assertEquals(QuizDifficulty.INTERMEDIATE, quiz.difficulty)
        assertEquals("category-1", quiz.categoryId)
        assertEquals("General Knowledge", quiz.categoryName)
        assertEquals(10, quiz.questionCount)
        assertEquals(30, quiz.timeLimit)
        assertEquals("https://example.com/image.jpg", quiz.imageUrl)
        assertEquals("2024-01-01T00:00:00Z", quiz.createdAt)
        assertEquals("2024-01-01T00:00:00Z", quiz.updatedAt)
    }

    @Test
    fun `Quiz should handle null time limit`() {
        val quiz = Quiz(
            id = "quiz-2",
            title = "Unlimited Quiz",
            description = "A quiz with no time limit",
            difficulty = QuizDifficulty.BEGINNER,
            categoryId = "category-2",
            categoryName = "Science",
            questionCount = 5,
            timeLimit = null,
            imageUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        assertNull(quiz.timeLimit)
        assertNull(quiz.imageUrl)
    }

    @Test
    fun `QuizDifficulty enum should contain all expected values`() {
        val difficulties = QuizDifficulty.values()
        
        assertEquals(3, difficulties.size)
        assertTrue(difficulties.contains(QuizDifficulty.BEGINNER))
        assertTrue(difficulties.contains(QuizDifficulty.INTERMEDIATE))
        assertTrue(difficulties.contains(QuizDifficulty.ADVANCED))
    }
}