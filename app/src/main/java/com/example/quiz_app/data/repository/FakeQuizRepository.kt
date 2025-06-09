package com.example.quiz_app.data.repository

import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import com.example.quiz_app.domain.Question
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
    
    override suspend fun getQuizById(id: String): Result<Quiz> {
        val quiz = sampleQuizzes.find { it.id == id }
        return if(quiz != null) Result.success(quiz) else Result.failure(Exception("Not found"))
    }

    override suspend fun searchQuizzes(query: String): Result<List<Quiz>> {
        return Result.success(sampleQuizzes.filter { it.title.contains(query, true) })
    }

    override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> {
        return Result.success(sampleQuizzes.filter { it.categoryId == categoryId })
    }

    override suspend fun getQuizDetail(quizId: String): Result<List<Question>> {
        val questions = when (quizId) {
            "1" -> listOf(
                Question(
                    id = "q1",
                    questionText = "Kotlinで変数を宣言するキーワードはどれ？",
                    options = listOf("var", "let", "const", "def"),
                    correctAnswerIndex = 0,
                    explanation = "Kotlinでは、変更可能な変数は`var`、変更不可能な変数は`val`を使用して宣言します。"
                ),
                Question(
                    id = "q2",
                    questionText = "AndroidでUIを構築するための現在推奨されているツールキットは？",
                    options = listOf("XML Layout", "Jetpack Compose", "React Native", "Flutter"),
                    correctAnswerIndex = 1,
                    explanation = "Jetpack ComposeはAndroidの現代的なUIツールキットで、宣言的UIの作成を可能にします。"
                ),
                Question(
                    id = "q3",
                    questionText = "Activityのライフサイクルメソッドの正しい順序は？",
                    options = listOf(
                        "onCreate → onStart → onResume",
                        "onStart → onCreate → onResume",
                        "onResume → onCreate → onStart",
                        "onCreate → onResume → onStart"
                    ),
                    correctAnswerIndex = 0,
                    explanation = "Activityのライフサイクルは onCreate() → onStart() → onResume() の順序で実行されます。"
                )
            )
            "2" -> listOf(
                Question(
                    id = "q4",
                    questionText = "Kotlinのcoroutinesで非同期処理を開始するために使用する関数は？",
                    options = listOf("async", "launch", "runBlocking", "withContext"),
                    correctAnswerIndex = 1,
                    explanation = "launch{}はfire-and-forget型の非同期処理を開始するために使用されます。"
                ),
                Question(
                    id = "q5",
                    questionText = "sealed classの主な用途は？",
                    options = listOf(
                        "継承の制限",
                        "型安全な状態管理", 
                        "enumの拡張版",
                        "上記すべて"
                    ),
                    correctAnswerIndex = 3,
                    explanation = "sealed classは継承を制限し、型安全な状態管理を可能にし、enumの機能を拡張したものです。"
                )
            )
            else -> listOf(
                Question(
                    id = "q_default",
                    questionText = "この問題はサンプル問題です。",
                    options = listOf("選択肢A", "選択肢B", "選択肢C", "選択肢D"),
                    correctAnswerIndex = 0,
                    explanation = "これはサンプルの解説です。"
                )
            )
        }
        return Result.success(questions)
    }
}