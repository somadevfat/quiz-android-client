package com.example.quiz_app.data.repository

import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import com.example.quiz_app.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeQuizRepository @Inject constructor() : QuizRepository {
    
    private val sampleQuizzes = listOf(
        Quiz(
            id = "1",
            title = "Android Development Fundamentals",
            description = "Test your knowledge of basic Android development concepts including Activities, Fragments, and the Activity lifecycle.",
            difficulty = QuizDifficulty.BEGINNER,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 15,
            timeLimit = 20,
            imageUrl = "https://example.com/android-quiz.jpg",
            createdAt = "2024-01-15T10:00:00Z",
            updatedAt = "2024-01-15T10:00:00Z"
        ),
        Quiz(
            id = "2",
            title = "Kotlin Advanced Features",
            description = "Dive deep into advanced Kotlin features including coroutines, sealed classes, and extension functions.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 20,
            timeLimit = 30,
            imageUrl = "https://example.com/kotlin-quiz.jpg",
            createdAt = "2024-01-16T14:30:00Z",
            updatedAt = "2024-01-16T14:30:00Z"
        ),
        Quiz(
            id = "3",
            title = "UI/UX Design Principles",
            description = "Learn about user interface design principles, Material Design guidelines, and user experience best practices.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "design",
            categoryName = "UI/UX Design",
            questionCount = 12,
            timeLimit = null,
            imageUrl = "https://example.com/ux-quiz.jpg",
            createdAt = "2024-01-17T09:15:00Z",
            updatedAt = "2024-01-17T09:15:00Z"
        ),
        Quiz(
            id = "4",
            title = "Clean Architecture Patterns",
            description = "Explore clean architecture patterns, MVVM, Repository pattern, and dependency injection in Android applications.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 25,
            timeLimit = 45,
            imageUrl = "https://example.com/architecture-quiz.jpg",
            createdAt = "2024-01-18T16:45:00Z",
            updatedAt = "2024-01-18T16:45:00Z"
        )
    )
    
    override fun getQuizzes(): Flow<List<Quiz>> {
        return flowOf(sampleQuizzes)
    }
    
    override fun getQuizById(id: String): Flow<Quiz?> {
        val quiz = sampleQuizzes.find { it.id == id }
        return flowOf(quiz)
    }
}