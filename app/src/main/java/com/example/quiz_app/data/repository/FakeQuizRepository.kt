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
        // Android Development Category (10 quizzes)
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
            title = "Android Activity Lifecycle",
            description = "Deep dive into Android Activity lifecycle methods and state management.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 12,
            timeLimit = 15,
            imageUrl = "https://example.com/activity-lifecycle.jpg",
            createdAt = "2024-01-20T11:00:00Z",
            updatedAt = "2024-01-20T11:00:00Z"
        ),
        Quiz(
            id = "3",
            title = "Android Fragments",
            description = "Master Android Fragments, Fragment lifecycle, and Fragment communication.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 18,
            timeLimit = 25,
            imageUrl = "https://example.com/fragments.jpg",
            createdAt = "2024-01-21T14:00:00Z",
            updatedAt = "2024-01-21T14:00:00Z"
        ),
        Quiz(
            id = "4",
            title = "Android Services & Broadcast Receivers",
            description = "Learn about background processing with Services and system communication with Broadcast Receivers.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 22,
            timeLimit = 35,
            imageUrl = "https://example.com/services.jpg",
            createdAt = "2024-01-22T09:00:00Z",
            updatedAt = "2024-01-22T09:00:00Z"
        ),
        Quiz(
            id = "5",
            title = "Android Data Storage",
            description = "Explore different data storage options: SharedPreferences, SQLite, Room Database, and File Storage.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 20,
            timeLimit = 30,
            imageUrl = "https://example.com/data-storage.jpg",
            createdAt = "2024-01-23T16:00:00Z",
            updatedAt = "2024-01-23T16:00:00Z"
        ),
        Quiz(
            id = "6",
            title = "Android Networking",
            description = "Master Android networking with Retrofit, OkHttp, and handling API responses.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 25,
            timeLimit = 40,
            imageUrl = "https://example.com/networking.jpg",
            createdAt = "2024-01-24T10:30:00Z",
            updatedAt = "2024-01-24T10:30:00Z"
        ),
        Quiz(
            id = "7",
            title = "Android Permissions",
            description = "Understand Android permission system, runtime permissions, and security best practices.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 16,
            timeLimit = 20,
            imageUrl = "https://example.com/permissions.jpg",
            createdAt = "2024-01-25T13:00:00Z",
            updatedAt = "2024-01-25T13:00:00Z"
        ),
        Quiz(
            id = "8",
            title = "Android Testing",
            description = "Learn Android testing strategies: Unit tests, Instrumentation tests, UI tests, and testing frameworks.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 28,
            timeLimit = 45,
            imageUrl = "https://example.com/testing.jpg",
            createdAt = "2024-01-26T15:30:00Z",
            updatedAt = "2024-01-26T15:30:00Z"
        ),
        Quiz(
            id = "9",
            title = "Android Performance Optimization",
            description = "Optimize Android app performance: memory management, battery optimization, and profiling tools.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 24,
            timeLimit = 40,
            imageUrl = "https://example.com/performance.jpg",
            createdAt = "2024-01-27T11:15:00Z",
            updatedAt = "2024-01-27T11:15:00Z"
        ),
        Quiz(
            id = "10",
            title = "Android Security",
            description = "Android security fundamentals: encryption, secure storage, authentication, and security best practices.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "android",
            categoryName = "Android Development",
            questionCount = 26,
            timeLimit = 45,
            imageUrl = "https://example.com/security.jpg",
            createdAt = "2024-01-28T14:45:00Z",
            updatedAt = "2024-01-28T14:45:00Z"
        ),

        // Kotlin Programming Category (12 quizzes)
        Quiz(
            id = "11",
            title = "Kotlin Basics",
            description = "Start your Kotlin journey with variables, data types, functions, and control structures.",
            difficulty = QuizDifficulty.BEGINNER,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 15,
            timeLimit = 20,
            imageUrl = "https://example.com/kotlin-basics.jpg",
            createdAt = "2024-02-01T09:00:00Z",
            updatedAt = "2024-02-01T09:00:00Z"
        ),
        Quiz(
            id = "12",
            title = "Kotlin Advanced Features",
            description = "Dive deep into advanced Kotlin features including coroutines, sealed classes, and extension functions.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 20,
            timeLimit = 30,
            imageUrl = "https://example.com/kotlin-advanced.jpg",
            createdAt = "2024-02-02T14:30:00Z",
            updatedAt = "2024-02-02T14:30:00Z"
        ),
        Quiz(
            id = "13",
            title = "Kotlin Coroutines",
            description = "Master asynchronous programming with Kotlin Coroutines: launch, async, suspend functions.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 22,
            timeLimit = 35,
            imageUrl = "https://example.com/coroutines.jpg",
            createdAt = "2024-02-03T10:15:00Z",
            updatedAt = "2024-02-03T10:15:00Z"
        ),
        Quiz(
            id = "14",
            title = "Kotlin Collections",
            description = "Work with Kotlin collections: List, Set, Map, and functional programming operations.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 18,
            timeLimit = 25,
            imageUrl = "https://example.com/collections.jpg",
            createdAt = "2024-02-04T16:00:00Z",
            updatedAt = "2024-02-04T16:00:00Z"
        ),
        Quiz(
            id = "15",
            title = "Kotlin Null Safety",
            description = "Understand Kotlin's null safety features: nullable types, safe calls, and Elvis operator.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 14,
            timeLimit = 18,
            imageUrl = "https://example.com/null-safety.jpg",
            createdAt = "2024-02-05T11:30:00Z",
            updatedAt = "2024-02-05T11:30:00Z"
        ),
        Quiz(
            id = "16",
            title = "Kotlin Object-Oriented Programming",
            description = "Classes, objects, inheritance, interfaces, and polymorphism in Kotlin.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 20,
            timeLimit = 30,
            imageUrl = "https://example.com/oop-kotlin.jpg",
            createdAt = "2024-02-06T13:45:00Z",
            updatedAt = "2024-02-06T13:45:00Z"
        ),
        Quiz(
            id = "17",
            title = "Kotlin Functional Programming",
            description = "Functional programming concepts in Kotlin: higher-order functions, lambdas, and scope functions.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 24,
            timeLimit = 40,
            imageUrl = "https://example.com/functional-kotlin.jpg",
            createdAt = "2024-02-07T15:20:00Z",
            updatedAt = "2024-02-07T15:20:00Z"
        ),
        Quiz(
            id = "18",
            title = "Kotlin Generics",
            description = "Type parameters, variance, and generic constraints in Kotlin.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 16,
            timeLimit = 25,
            imageUrl = "https://example.com/generics.jpg",
            createdAt = "2024-02-08T09:10:00Z",
            updatedAt = "2024-02-08T09:10:00Z"
        ),
        Quiz(
            id = "19",
            title = "Kotlin DSL",
            description = "Domain Specific Languages in Kotlin: builders, type-safe builders, and DSL design.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 19,
            timeLimit = 30,
            imageUrl = "https://example.com/dsl.jpg",
            createdAt = "2024-02-09T12:00:00Z",
            updatedAt = "2024-02-09T12:00:00Z"
        ),
        Quiz(
            id = "20",
            title = "Kotlin Multiplatform",
            description = "Kotlin Multiplatform Mobile: shared code, expect/actual declarations, and platform-specific implementations.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 21,
            timeLimit = 35,
            imageUrl = "https://example.com/multiplatform.jpg",
            createdAt = "2024-02-10T14:30:00Z",
            updatedAt = "2024-02-10T14:30:00Z"
        ),
        Quiz(
            id = "21",
            title = "Kotlin Reflection",
            description = "Runtime inspection of Kotlin code: KClass, KFunction, annotations, and metaprogramming.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 17,
            timeLimit = 28,
            imageUrl = "https://example.com/reflection.jpg",
            createdAt = "2024-02-11T16:45:00Z",
            updatedAt = "2024-02-11T16:45:00Z"
        ),
        Quiz(
            id = "22",
            title = "Kotlin Testing",
            description = "Testing Kotlin code: JUnit, MockK, testing coroutines, and test-driven development.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "kotlin",
            categoryName = "Kotlin Programming",
            questionCount = 23,
            timeLimit = 35,
            imageUrl = "https://example.com/kotlin-testing.jpg",
            createdAt = "2024-02-12T10:20:00Z",
            updatedAt = "2024-02-12T10:20:00Z"
        ),

        // Jetpack Compose Category (8 quizzes)
        Quiz(
            id = "23",
            title = "Jetpack Compose Basics",
            description = "Introduction to Jetpack Compose: Composables, state, and declarative UI.",
            difficulty = QuizDifficulty.BEGINNER,
            categoryId = "compose",
            categoryName = "Jetpack Compose",
            questionCount = 16,
            timeLimit = 22,
            imageUrl = "https://example.com/compose-basics.jpg",
            createdAt = "2024-02-13T11:00:00Z",
            updatedAt = "2024-02-13T11:00:00Z"
        ),
        Quiz(
            id = "24",
            title = "Compose State Management",
            description = "Managing state in Compose: remember, State, MutableState, and state hoisting.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "compose",
            categoryName = "Jetpack Compose",
            questionCount = 19,
            timeLimit = 28,
            imageUrl = "https://example.com/compose-state.jpg",
            createdAt = "2024-02-14T13:15:00Z",
            updatedAt = "2024-02-14T13:15:00Z"
        ),
        Quiz(
            id = "25",
            title = "Compose Layouts",
            description = "Layout systems in Compose: Column, Row, Box, ConstraintLayout, and custom layouts.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "compose",
            categoryName = "Jetpack Compose",
            questionCount = 21,
            timeLimit = 32,
            imageUrl = "https://example.com/compose-layouts.jpg",
            createdAt = "2024-02-15T15:30:00Z",
            updatedAt = "2024-02-15T15:30:00Z"
        ),
        Quiz(
            id = "26",
            title = "Compose Navigation",
            description = "Navigation in Compose: NavController, NavHost, navigation arguments, and deep links.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "compose",
            categoryName = "Jetpack Compose",
            questionCount = 18,
            timeLimit = 26,
            imageUrl = "https://example.com/compose-navigation.jpg",
            createdAt = "2024-02-16T09:45:00Z",
            updatedAt = "2024-02-16T09:45:00Z"
        ),
        Quiz(
            id = "27",
            title = "Compose Animation",
            description = "Animations in Compose: animateContentSize, Transition, AnimatedVisibility, and custom animations.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "compose",
            categoryName = "Jetpack Compose",
            questionCount = 20,
            timeLimit = 30,
            imageUrl = "https://example.com/compose-animation.jpg",
            createdAt = "2024-02-17T14:20:00Z",
            updatedAt = "2024-02-17T14:20:00Z"
        ),
        Quiz(
            id = "28",
            title = "Compose Material Design",
            description = "Material Design in Compose: Material 3, theming, colors, typography, and components.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "compose",
            categoryName = "Jetpack Compose",
            questionCount = 17,
            timeLimit = 25,
            imageUrl = "https://example.com/compose-material.jpg",
            createdAt = "2024-02-18T12:10:00Z",
            updatedAt = "2024-02-18T12:10:00Z"
        ),
        Quiz(
            id = "29",
            title = "Compose Testing",
            description = "Testing Compose UI: ComposeTestRule, semantic nodes, UI testing, and testing strategies.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "compose",
            categoryName = "Jetpack Compose",
            questionCount = 22,
            timeLimit = 35,
            imageUrl = "https://example.com/compose-testing.jpg",
            createdAt = "2024-02-19T16:00:00Z",
            updatedAt = "2024-02-19T16:00:00Z"
        ),
        Quiz(
            id = "30",
            title = "Compose Performance",
            description = "Optimizing Compose performance: recomposition, derivedStateOf, and performance best practices.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "compose",
            categoryName = "Jetpack Compose",
            questionCount = 19,
            timeLimit = 30,
            imageUrl = "https://example.com/compose-performance.jpg",
            createdAt = "2024-02-20T10:30:00Z",
            updatedAt = "2024-02-20T10:30:00Z"
        ),

        // Software Architecture Category (10 quizzes)
        Quiz(
            id = "31",
            title = "Clean Architecture Patterns",
            description = "Explore clean architecture patterns, MVVM, Repository pattern, and dependency injection in Android applications.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 25,
            timeLimit = 45,
            imageUrl = "https://example.com/clean-architecture.jpg",
            createdAt = "2024-02-21T11:45:00Z",
            updatedAt = "2024-02-21T11:45:00Z"
        ),
        Quiz(
            id = "32",
            title = "MVVM Architecture",
            description = "Model-View-ViewModel pattern: ViewModel, LiveData, DataBinding, and state management.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 20,
            timeLimit = 30,
            imageUrl = "https://example.com/mvvm.jpg",
            createdAt = "2024-02-22T13:20:00Z",
            updatedAt = "2024-02-22T13:20:00Z"
        ),
        Quiz(
            id = "33",
            title = "Repository Pattern",
            description = "Data layer abstraction: Repository pattern, data sources, and caching strategies.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 18,
            timeLimit = 28,
            imageUrl = "https://example.com/repository.jpg",
            createdAt = "2024-02-23T15:00:00Z",
            updatedAt = "2024-02-23T15:00:00Z"
        ),
        Quiz(
            id = "34",
            title = "Dependency Injection",
            description = "DI principles, Dagger Hilt, manual DI, and dependency inversion principle.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 23,
            timeLimit = 38,
            imageUrl = "https://example.com/dependency-injection.jpg",
            createdAt = "2024-02-24T09:30:00Z",
            updatedAt = "2024-02-24T09:30:00Z"
        ),
        Quiz(
            id = "35",
            title = "Modularization",
            description = "App modularization: feature modules, library modules, and module dependencies.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 21,
            timeLimit = 35,
            imageUrl = "https://example.com/modularization.jpg",
            createdAt = "2024-02-25T14:15:00Z",
            updatedAt = "2024-02-25T14:15:00Z"
        ),
        Quiz(
            id = "36",
            title = "Design Patterns",
            description = "Common design patterns: Singleton, Observer, Factory, Builder, and Strategy patterns.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 24,
            timeLimit = 40,
            imageUrl = "https://example.com/design-patterns.jpg",
            createdAt = "2024-02-26T12:00:00Z",
            updatedAt = "2024-02-26T12:00:00Z"
        ),
        Quiz(
            id = "37",
            title = "SOLID Principles",
            description = "SOLID principles in Android development: SRP, OCP, LSP, ISP, and DIP.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 20,
            timeLimit = 32,
            imageUrl = "https://example.com/solid.jpg",
            createdAt = "2024-02-27T16:30:00Z",
            updatedAt = "2024-02-27T16:30:00Z"
        ),
        Quiz(
            id = "38",
            title = "MVI Architecture",
            description = "Model-View-Intent pattern: unidirectional data flow, immutable state, and intent handling.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 19,
            timeLimit = 30,
            imageUrl = "https://example.com/mvi.jpg",
            createdAt = "2024-02-28T10:45:00Z",
            updatedAt = "2024-02-28T10:45:00Z"
        ),
        Quiz(
            id = "39",
            title = "Domain-Driven Design",
            description = "DDD concepts: entities, value objects, repositories, and domain services in Android.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 22,
            timeLimit = 35,
            imageUrl = "https://example.com/ddd.jpg",
            createdAt = "2024-03-01T13:10:00Z",
            updatedAt = "2024-03-01T13:10:00Z"
        ),
        Quiz(
            id = "40",
            title = "Microservices & Android",
            description = "Integrating Android apps with microservices: API design, service communication, and resilience patterns.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "architecture",
            categoryName = "Software Architecture",
            questionCount = 26,
            timeLimit = 42,
            imageUrl = "https://example.com/microservices.jpg",
            createdAt = "2024-03-02T15:40:00Z",
            updatedAt = "2024-03-02T15:40:00Z"
        ),

        // UI/UX Design Category (8 quizzes)
        Quiz(
            id = "41",
            title = "UI/UX Design Principles",
            description = "Learn about user interface design principles, Material Design guidelines, and user experience best practices.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "design",
            categoryName = "UI/UX Design",
            questionCount = 12,
            timeLimit = null,
            imageUrl = "https://example.com/ux-principles.jpg",
            createdAt = "2024-03-03T09:15:00Z",
            updatedAt = "2024-03-03T09:15:00Z"
        ),
        Quiz(
            id = "42",
            title = "Material Design 3",
            description = "Material Design 3 guidelines: Dynamic Color, typography, elevation, and component specifications.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "design",
            categoryName = "UI/UX Design",
            questionCount = 18,
            timeLimit = 26,
            imageUrl = "https://example.com/material-design-3.jpg",
            createdAt = "2024-03-04T11:30:00Z",
            updatedAt = "2024-03-04T11:30:00Z"
        ),
        Quiz(
            id = "43",
            title = "Accessibility in Android",
            description = "Android accessibility: TalkBack, content descriptions, accessibility services, and inclusive design.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "design",
            categoryName = "UI/UX Design",
            questionCount = 16,
            timeLimit = 24,
            imageUrl = "https://example.com/accessibility.jpg",
            createdAt = "2024-03-05T14:20:00Z",
            updatedAt = "2024-03-05T14:20:00Z"
        ),
        Quiz(
            id = "44",
            title = "Mobile Design Patterns",
            description = "Common mobile design patterns: navigation patterns, input patterns, and interaction patterns.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "design",
            categoryName = "UI/UX Design",
            questionCount = 20,
            timeLimit = 30,
            imageUrl = "https://example.com/design-patterns-mobile.jpg",
            createdAt = "2024-03-06T16:00:00Z",
            updatedAt = "2024-03-06T16:00:00Z"
        ),
        Quiz(
            id = "45",
            title = "User Research & Testing",
            description = "User research methods, usability testing, A/B testing, and user feedback collection.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "design",
            categoryName = "UI/UX Design",
            questionCount = 17,
            timeLimit = 25,
            imageUrl = "https://example.com/user-research.jpg",
            createdAt = "2024-03-07T12:45:00Z",
            updatedAt = "2024-03-07T12:45:00Z"
        ),
        Quiz(
            id = "46",
            title = "Information Architecture",
            description = "Organizing information: card sorting, site maps, user flows, and content strategy.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "design",
            categoryName = "UI/UX Design",
            questionCount = 15,
            timeLimit = 22,
            imageUrl = "https://example.com/information-architecture.jpg",
            createdAt = "2024-03-08T10:10:00Z",
            updatedAt = "2024-03-08T10:10:00Z"
        ),
        Quiz(
            id = "47",
            title = "Design Systems",
            description = "Building and maintaining design systems: component libraries, design tokens, and style guides.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "design",
            categoryName = "UI/UX Design",
            questionCount = 21,
            timeLimit = 33,
            imageUrl = "https://example.com/design-systems.jpg",
            createdAt = "2024-03-09T13:25:00Z",
            updatedAt = "2024-03-09T13:25:00Z"
        ),
        Quiz(
            id = "48",
            title = "Prototyping & Wireframing",
            description = "Creating prototypes and wireframes: tools, techniques, and best practices for design iteration.",
            difficulty = QuizDifficulty.BEGINNER,
            categoryId = "design",
            categoryName = "UI/UX Design",
            questionCount = 14,
            timeLimit = 20,
            imageUrl = "https://example.com/prototyping.jpg",
            createdAt = "2024-03-10T15:50:00Z",
            updatedAt = "2024-03-10T15:50:00Z"
        ),

        // Testing Category (6 quizzes)
        Quiz(
            id = "49",
            title = "Android Testing Fundamentals",
            description = "Testing basics: unit tests, integration tests, UI tests, and testing pyramid.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "testing",
            categoryName = "Testing",
            questionCount = 20,
            timeLimit = 30,
            imageUrl = "https://example.com/testing-fundamentals.jpg",
            createdAt = "2024-03-11T09:00:00Z",
            updatedAt = "2024-03-11T09:00:00Z"
        ),
        Quiz(
            id = "50",
            title = "JUnit & Mockito",
            description = "Unit testing with JUnit 5, Mockito for mocking, and test-driven development practices.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "testing",
            categoryName = "Testing",
            questionCount = 18,
            timeLimit = 28,
            imageUrl = "https://example.com/junit-mockito.jpg",
            createdAt = "2024-03-12T11:20:00Z",
            updatedAt = "2024-03-12T11:20:00Z"
        ),
        Quiz(
            id = "51",
            title = "Espresso UI Testing",
            description = "UI testing with Espresso: ViewMatchers, ViewActions, ViewAssertions, and test automation.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "testing",
            categoryName = "Testing",
            questionCount = 22,
            timeLimit = 35,
            imageUrl = "https://example.com/espresso.jpg",
            createdAt = "2024-03-13T14:10:00Z",
            updatedAt = "2024-03-13T14:10:00Z"
        ),
        Quiz(
            id = "52",
            title = "Testing Coroutines",
            description = "Testing asynchronous code: TestCoroutineDispatcher, runBlockingTest, and coroutine testing best practices.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "testing",
            categoryName = "Testing",
            questionCount = 16,
            timeLimit = 25,
            imageUrl = "https://example.com/testing-coroutines.jpg",
            createdAt = "2024-03-14T16:30:00Z",
            updatedAt = "2024-03-14T16:30:00Z"
        ),
        Quiz(
            id = "53",
            title = "Test Doubles & Mocking",
            description = "Test doubles: mocks, stubs, fakes, spies, and when to use each type.",
            difficulty = QuizDifficulty.INTERMEDIATE,
            categoryId = "testing",
            categoryName = "Testing",
            questionCount = 17,
            timeLimit = 26,
            imageUrl = "https://example.com/test-doubles.jpg",
            createdAt = "2024-03-15T12:50:00Z",
            updatedAt = "2024-03-15T12:50:00Z"
        ),
        Quiz(
            id = "54",
            title = "Continuous Integration Testing",
            description = "CI/CD for Android testing: automated testing pipelines, test reports, and quality gates.",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "testing",
            categoryName = "Testing",
            questionCount = 19,
            timeLimit = 30,
            imageUrl = "https://example.com/ci-testing.jpg",
            createdAt = "2024-03-16T10:15:00Z",
            updatedAt = "2024-03-16T10:15:00Z"
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
            "1" -> generateAndroidFundamentalsQuestions()
            "2" -> generateActivityLifecycleQuestions()
            "3" -> generateFragmentsQuestions()
            "11" -> generateKotlinBasicsQuestions()
            "12" -> generateKotlinAdvancedQuestions()
            "13" -> generateCoroutinesQuestions()
            "23" -> generateComposeBasicsQuestions()
            "24" -> generateComposeStateQuestions()
            "31" -> generateCleanArchitectureQuestions()
            "41" -> generateUIUXPrinciplesQuestions()
            else -> generateDefaultQuestions(quizId)
        }
        return Result.success(questions)
    }

    private fun generateAndroidFundamentalsQuestions(): List<Question> = listOf(
        Question(
            id = "q1_1",
            questionText = "Androidアプリケーションの基本構成要素として正しくないものは？",
            options = listOf("Activity", "Service", "Fragment", "Content Provider"),
            correctAnswerIndex = 2,
            explanation = "Fragmentは基本構成要素ではなく、ActivityやServiceが基本構成要素です。Fragmentは画面の一部を表すUIコンポーネントです。"
        ),
        Question(
            id = "q1_2",
            questionText = "AndroidManifest.xmlファイルの主な役割は？",
            options = listOf("アプリのデザインを定義", "アプリの構成要素を宣言", "アプリのロジックを記述", "アプリのデータを保存"),
            correctAnswerIndex = 1,
            explanation = "AndroidManifest.xmlは、アプリの構成要素（Activity、Service等）、権限、メタデータを宣言するファイルです。"
        ),
        Question(
            id = "q1_3",
            questionText = "Activityのライフサイクルメソッドの正しい順序は？",
            options = listOf(
                "onCreate → onStart → onResume",
                "onStart → onCreate → onResume",
                "onResume → onCreate → onStart",
                "onCreate → onResume → onStart"
            ),
            correctAnswerIndex = 0,
            explanation = "Activityのライフサイクルは onCreate() → onStart() → onResume() の順序で実行されます。"
        ),
        Question(
            id = "q1_4",
            questionText = "Intent の主な用途として正しくないものは？",
            options = listOf("Activity間の遷移", "Serviceの開始", "データベースアクセス", "Broadcast送信"),
            correctAnswerIndex = 2,
            explanation = "Intentはコンポーネント間の通信に使用されますが、データベースアクセスには直接関係ありません。"
        ),
        Question(
            id = "q1_5",
            questionText = "ViewGroup の説明として正しいものは？",
            options = listOf(
                "単一のUIコンポーネント",
                "他のViewを含むコンテナ",
                "データベーステーブル",
                "ネットワーク接続"
            ),
            correctAnswerIndex = 1,
            explanation = "ViewGroupは他のView（子View）を含むことができるコンテナクラスです。LinearLayout、RelativeLayoutなどが代表例です。"
        )
    )

    private fun generateActivityLifecycleQuestions(): List<Question> = listOf(
        Question(
            id = "q2_1",
            questionText = "onPause()メソッドが呼ばれるタイミングは？",
            options = listOf(
                "Activityが完全に停止した時",
                "他のActivityがフォーカスを得た時",
                "Activityが破棄される時",
                "アプリが初回起動された時"
            ),
            correctAnswerIndex = 1,
            explanation = "onPause()は他のActivityがフォーカスを得て、現在のActivityが一時停止状態になった時に呼ばれます。"
        ),
        Question(
            id = "q2_2",
            questionText = "Activity状態の保存に使用するメソッドは？",
            options = listOf("onSave()", "onSaveInstanceState()", "onStore()", "onPreserve()"),
            correctAnswerIndex = 1,
            explanation = "onSaveInstanceState()は画面回転や一時的な破棄時にActivity状態を保存するためのメソッドです。"
        ),
        Question(
            id = "q2_3",
            questionText = "onDestroy()の後に呼ばれ得るメソッドは？",
            options = listOf("onCreate()", "onStart()", "onResume()", "呼ばれることはない"),
            correctAnswerIndex = 0,
            explanation = "onDestroy()後、新しいActivityインスタンスが作成される場合、再びonCreate()が呼ばれる可能性があります。"
        )
    )

    private fun generateFragmentsQuestions(): List<Question> = listOf(
        Question(
            id = "q3_1",
            questionText = "Fragmentの主な利点として正しくないものは？",
            options = listOf(
                "UI の再利用性",
                "タブレット対応の柔軟性",
                "独立したライフサイクル",
                "自動的なデータ永続化"
            ),
            correctAnswerIndex = 3,
            explanation = "Fragmentは自動的なデータ永続化機能を持っていません。データの永続化は別途実装する必要があります。"
        ),
        Question(
            id = "q3_2",
            questionText = "FragmentとActivityの通信方法として推奨されるものは？",
            options = listOf(
                "直接参照を取得",
                "SharedPreferences使用",
                "ViewModel経由",
                "staticメソッド使用"
            ),
            correctAnswerIndex = 2,
            explanation = "ViewModelを使用することで、Fragment-Activity間で安全にデータを共有できます。"
        )
    )

    private fun generateKotlinBasicsQuestions(): List<Question> = listOf(
        Question(
            id = "q11_1",
            questionText = "Kotlinで変数を宣言するキーワードはどれ？",
            options = listOf("var", "let", "const", "def"),
            correctAnswerIndex = 0,
            explanation = "Kotlinでは、変更可能な変数は`var`、変更不可能な変数は`val`を使用して宣言します。"
        ),
        Question(
            id = "q11_2",
            questionText = "Kotlinのnull安全性に関する説明として正しいものは？",
            options = listOf(
                "すべての変数がnullを許可",
                "明示的にnullを許可した変数のみnull可能",
                "nullは使用できない",
                "自動的にnullチェックが行われる"
            ),
            correctAnswerIndex = 1,
            explanation = "Kotlinでは型に?を付けることでnullable型を明示的に宣言する必要があります。"
        ),
        Question(
            id = "q11_3",
            questionText = "Kotlinの when式の特徴として正しいものは？",
            options = listOf(
                "if文と同じ機能",
                "式として値を返せる",
                "条件は2つまで",
                "数値のみ対応"
            ),
            correctAnswerIndex = 1,
            explanation = "when式は値を返すことができ、Javaのswitch文より強力で柔軟です。"
        )
    )

    private fun generateKotlinAdvancedQuestions(): List<Question> = listOf(
        Question(
            id = "q12_1",
            questionText = "Kotlinのcoroutinesで非同期処理を開始するために使用する関数は？",
            options = listOf("async", "launch", "runBlocking", "withContext"),
            correctAnswerIndex = 1,
            explanation = "launch{}はfire-and-forget型の非同期処理を開始するために使用されます。"
        ),
        Question(
            id = "q12_2",
            questionText = "sealed classの主な用途は？",
            options = listOf(
                "継承の制限",
                "型安全な状態管理", 
                "enumの拡張版",
                "上記すべて"
            ),
            correctAnswerIndex = 3,
            explanation = "sealed classは継承を制限し、型安全な状態管理を可能にし、enumの機能を拡張したものです。"
        ),
        Question(
            id = "q12_3",
            questionText = "data classの自動生成されるメソッドに含まれないものは？",
            options = listOf("equals()", "hashCode()", "toString()", "clone()"),
            correctAnswerIndex = 3,
            explanation = "data classはequals()、hashCode()、toString()、copy()を自動生成しますが、clone()は生成されません。"
        )
    )

    private fun generateCoroutinesQuestions(): List<Question> = listOf(
        Question(
            id = "q13_1",
            questionText = "suspendキーワードの役割は？",
            options = listOf(
                "関数を一時停止可能にする",
                "関数を非同期実行する",
                "関数をバックグラウンドで実行",
                "関数の実行を遅延させる"
            ),
            correctAnswerIndex = 0,
            explanation = "suspend修飾子は関数がコルーチン内で一時停止可能であることを示します。"
        ),
        Question(
            id = "q13_2",
            questionText = "CoroutineScopeの主な目的は？",
            options = listOf(
                "コルーチンの実行速度向上",
                "コルーチンのライフサイクル管理",
                "コルーチンのデバッグ",
                "コルーチンのパフォーマンス監視"
            ),
            correctAnswerIndex = 1,
            explanation = "CoroutineScopeはコルーチンのライフサイクルを管理し、適切なタイミングでキャンセルできます。"
        )
    )

    private fun generateComposeBasicsQuestions(): List<Question> = listOf(
        Question(
            id = "q23_1",
            questionText = "Jetpack Composeの基本原則として正しいものは？",
            options = listOf("命令的UI", "宣言的UI", "手続き的UI", "関数型UI"),
            correctAnswerIndex = 1,
            explanation = "Jetpack Composeは宣言的UIツールキットで、UI状態に基づいてUIを宣言的に記述します。"
        ),
        Question(
            id = "q23_2",
            questionText = "@Composableアノテーションの目的は？",
            options = listOf(
                "関数をCompose UIとして標識",
                "関数のパフォーマンス向上",
                "関数のデバッグ支援",
                "関数の並列実行"
            ),
            correctAnswerIndex = 0,
            explanation = "@ComposableアノテーションはCompose UIを構築する関数であることをコンパイラに伝えます。"
        )
    )

    private fun generateComposeStateQuestions(): List<Question> = listOf(
        Question(
            id = "q24_1",
            questionText = "Composeで状態を記憶するために使用する関数は？",
            options = listOf("remember", "recall", "store", "save"),
            correctAnswerIndex = 0,
            explanation = "remember関数は再コンポジション間で値を保持するために使用されます。"
        ),
        Question(
            id = "q24_2",
            questionText = "state hoistingの主な利点は？",
            options = listOf(
                "パフォーマンスの向上",
                "状態の一元管理",
                "メモリ使用量削減",
                "UIの高速描画"
            ),
            correctAnswerIndex = 1,
            explanation = "state hoistingにより状態を上位コンポーネントで管理し、再利用性とテスタビリティが向上します。"
        )
    )

    private fun generateCleanArchitectureQuestions(): List<Question> = listOf(
        Question(
            id = "q31_1",
            questionText = "Clean Architectureの依存関係ルールとは？",
            options = listOf(
                "外側の層が内側の層に依存",
                "内側の層が外側の層に依存", 
                "すべての層が相互依存",
                "層間の依存関係は不要"
            ),
            correctAnswerIndex = 0,
            explanation = "Clean Architectureでは外側の層（UI、DB）が内側の層（ビジネスロジック）に依存します。"
        ),
        Question(
            id = "q31_2",
            questionText = "Repository パターンの主な目的は？",
            options = listOf(
                "データアクセスの抽象化",
                "UIの改善",
                "パフォーマンス向上",
                "メモリ管理"
            ),
            correctAnswerIndex = 0,
            explanation = "Repositoryパターンはデータアクセス層を抽象化し、ビジネスロジックからデータソースの詳細を隠蔽します。"
        )
    )

    private fun generateUIUXPrinciplesQuestions(): List<Question> = listOf(
        Question(
            id = "q41_1",
            questionText = "Material Designの基本原則に含まれないものは？",
            options = listOf("Material metaphor", "Bold graphic", "Platform specific", "Meaningful motion"),
            correctAnswerIndex = 2,
            explanation = "Material Designは Material metaphor、Bold graphic、Meaningful motionが基本原則で、プラットフォーム特化は原則ではありません。"
        ),
        Question(
            id = "q41_2",
            questionText = "ユーザビリティの5つの要素に含まれないものは？",
            options = listOf("学習しやすさ", "効率性", "デザインの美しさ", "記憶しやすさ"),
            correctAnswerIndex = 2,
            explanation = "ユーザビリティの5要素は学習性、効率性、記憶性、エラー、満足度で、美しさは直接的な要素ではありません。"
        )
    )

    private fun generateDefaultQuestions(quizId: String): List<Question> = listOf(
        Question(
            id = "q_${quizId}_1",
            questionText = "このクイズに関する基本的な質問です。最も適切な選択肢を選んでください。",
            options = listOf("選択肢A", "選択肢B", "選択肢C", "選択肢D"),
            correctAnswerIndex = 1,
            explanation = "これはサンプル問題の解説です。実際のクイズでは、この分野に特化した詳細な解説が提供されます。"
        ),
        Question(
            id = "q_${quizId}_2",
            questionText = "実践的な応用問題です。理論だけでなく実装経験も考慮して答えてください。",
            options = listOf("実装方法A", "実装方法B", "実装方法C", "実装方法D"),
            correctAnswerIndex = 2,
            explanation = "実際の開発現場では、この方法が最も効率的で保守性の高いアプローチとされています。"
        ),
        Question(
            id = "q_${quizId}_3", 
            questionText = "上級者向けの問題です。複数の概念を組み合わせて考える必要があります。",
            options = listOf("統合アプローチA", "統合アプローチB", "統合アプローチC", "統合アプローチD"),
            correctAnswerIndex = 0,
            explanation = "この問題では複数の技術概念を統合的に理解することが重要です。"
        )
    )
}