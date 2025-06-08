package com.example.quiz_app.data.remote.dto

import com.example.quiz_app.domain.QuizDifficulty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class QuizDtoTest {

    @Test
    fun `toDomain should correctly map QuizDto to Quiz domain model`() {
        val quizDto = QuizDto(
            id = 123,
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

        val quiz = quizDto.toDomain()

        assertEquals("123", quiz.id)
        assertEquals("Android Basics - Views", quiz.title)
        assertEquals("What is an Activity?", quiz.description)
        assertEquals(QuizDifficulty.BEGINNER, quiz.difficulty)
        assertEquals("Q001", quiz.categoryId)
        assertEquals("Views", quiz.categoryName)
        assertEquals(4, quiz.questionCount)
        assertNull(quiz.timeLimit)
        assertNull(quiz.imageUrl)
        assertEquals("", quiz.createdAt)
        assertEquals("", quiz.updatedAt)
    }

    @Test
    fun `toDomain should map intermediate difficulty correctly`() {
        val quizDto = QuizDto(
            id = 456,
            qid = "Q002",
            chapter = "Kotlin",
            category = "Coroutines",
            difficulty = "中級",
            choices = listOf("A", "B"),
            code = null,
            questionText = "What is a suspend function?",
            explanation = "A suspend function can be paused and resumed.",
            questionCategory = "Kotlin"
        )

        val quiz = quizDto.toDomain()

        assertEquals(QuizDifficulty.INTERMEDIATE, quiz.difficulty)
        assertEquals(2, quiz.questionCount)
    }

    @Test
    fun `toDomain should map advanced difficulty correctly`() {
        val quizDto = QuizDto(
            id = 789,
            qid = "Q003",
            chapter = "Architecture",
            category = "MVVM",
            difficulty = "上級",
            choices = listOf("Option 1"),
            code = "class ViewModel",
            questionText = "Explain MVVM pattern",
            explanation = "MVVM separates UI from business logic.",
            questionCategory = "Architecture"
        )

        val quiz = quizDto.toDomain()

        assertEquals(QuizDifficulty.ADVANCED, quiz.difficulty)
        assertEquals(1, quiz.questionCount)
    }

    @Test
    fun `toDomain should default to BEGINNER for unknown difficulty`() {
        val quizDto = QuizDto(
            id = 999,
            qid = "Q004",
            chapter = "Unknown",
            category = "Test",
            difficulty = "不明",
            choices = listOf("Yes", "No"),
            code = null,
            questionText = "Is this unknown?",
            explanation = "This tests unknown difficulty mapping.",
            questionCategory = "Test"
        )

        val quiz = quizDto.toDomain()

        assertEquals(QuizDifficulty.BEGINNER, quiz.difficulty)
    }

    @Test
    fun `toDomain should handle empty choices list`() {
        val quizDto = QuizDto(
            id = 111,
            qid = "Q005",
            chapter = "Test",
            category = "Empty",
            difficulty = "初級",
            choices = emptyList(),
            code = null,
            questionText = "No choices?",
            explanation = "This has no choices.",
            questionCategory = "Test"
        )

        val quiz = quizDto.toDomain()

        assertEquals(0, quiz.questionCount)
    }

    @Test
    fun `toDomain should handle null code field`() {
        val quizDto = QuizDto(
            id = 222,
            qid = "Q006",
            chapter = "Basic",
            category = "Theory",
            difficulty = "初級",
            choices = listOf("True", "False"),
            code = null,
            questionText = "Is code optional?",
            explanation = "Yes, code can be null.",
            questionCategory = "Theory"
        )

        val quiz = quizDto.toDomain()

        assertEquals("Basic - Theory", quiz.title)
        assertEquals("Is code optional?", quiz.description)
    }
}