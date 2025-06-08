package com.example.quiz_app.presentation.quiz_list

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import com.example.quiz_app.domain.repository.QuizRepository
import com.example.quiz_app.ui.theme.QuizAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class QuizListScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var repository: QuizRepository

    private val sampleQuizzes = listOf(
        Quiz(
            id = "1",
            title = "Android Development",
            description = "Learn Android basics",
            difficulty = QuizDifficulty.BEGINNER,
            categoryId = "android",
            categoryName = "Android",
            questionCount = 10,
            timeLimit = 15,
            imageUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Quiz(
            id = "2",
            title = "Kotlin Advanced",
            description = "Advanced Kotlin concepts",
            difficulty = QuizDifficulty.ADVANCED,
            categoryId = "kotlin",
            categoryName = "Kotlin",
            questionCount = 20,
            timeLimit = null,
            imageUrl = null,
            createdAt = "2024-01-02T00:00:00Z",
            updatedAt = "2024-01-02T00:00:00Z"
        )
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun quizListScreen_displaysTitle() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen()
            }
        }

        composeTestRule
            .onNodeWithText("Quiz Library")
            .assertIsDisplayed()
    }

    @Test
    fun quizListScreen_displaysLoadingState() {
        val mockRepository = mockk<QuizRepository>()
        every { mockRepository.getQuizzes() } returns flowOf() // Never emits

        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(mockRepository)
                )
            }
        }

        composeTestRule
            .onNode(hasTestTag("loading_indicator") or hasContentDescription("Loading"))
            .assertExists()
    }

    @Test
    fun quizListScreen_displaysQuizzes() {
        val mockRepository = mockk<QuizRepository>()
        every { mockRepository.getQuizzes() } returns flowOf(sampleQuizzes)

        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(mockRepository)
                )
            }
        }

        // Wait for loading to complete and verify quiz items are displayed
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Android Development")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Kotlin Advanced")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Learn Android basics")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Advanced Kotlin concepts")
            .assertIsDisplayed()
    }

    @Test
    fun quizListScreen_displaysQuizDetails() {
        val mockRepository = mockk<QuizRepository>()
        every { mockRepository.getQuizzes() } returns flowOf(sampleQuizzes)

        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(mockRepository)
                )
            }
        }

        composeTestRule.waitForIdle()

        // Check difficulty chips
        composeTestRule
            .onNodeWithText("Beginner")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Advanced")
            .assertIsDisplayed()

        // Check question count and time limit
        composeTestRule
            .onNodeWithText("10 questions")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("20 questions")
            .assertIsDisplayed()

        composeTestRule
            .onNode(hasText("15 min"))
            .assertIsDisplayed()

        // Check category names
        composeTestRule
            .onNodeWithText("Android")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Kotlin")
            .assertIsDisplayed()
    }

    @Test
    fun quizListScreen_displaysErrorState() {
        val mockRepository = mockk<QuizRepository>()
        every { mockRepository.getQuizzes() } throws RuntimeException("Network error")

        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(mockRepository)
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Oops! Something went wrong")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Network error")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Try Again")
            .assertIsDisplayed()
    }

    @Test
    fun quizListScreen_retryButtonWorksOnError() {
        val mockRepository = mockk<QuizRepository>()
        every { mockRepository.getQuizzes() } throws RuntimeException("Network error") andThen flowOf(sampleQuizzes)

        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(mockRepository)
                )
            }
        }

        composeTestRule.waitForIdle()

        // Verify error state is displayed
        composeTestRule
            .onNodeWithText("Try Again")
            .assertIsDisplayed()

        // Click retry button
        composeTestRule
            .onNodeWithText("Try Again")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify success state after retry
        composeTestRule
            .onNodeWithText("Android Development")
            .assertIsDisplayed()
    }

    @Test
    fun quizCard_startQuizButtonIsClickable() {
        val mockRepository = mockk<QuizRepository>()
        every { mockRepository.getQuizzes() } returns flowOf(sampleQuizzes)

        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(mockRepository)
                )
            }
        }

        composeTestRule.waitForIdle()

        // Find and click the Start Quiz button
        composeTestRule
            .onAllNodesWithText("Start Quiz")
            .onFirst()
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    @Test
    fun quizListScreen_displaysEmptyState() {
        val mockRepository = mockk<QuizRepository>()
        every { mockRepository.getQuizzes() } returns flowOf(emptyList())

        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(mockRepository)
                )
            }
        }

        composeTestRule.waitForIdle()

        // Should show title but no quiz items
        composeTestRule
            .onNodeWithText("Quiz Library")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Android Development")
            .assertDoesNotExist()
    }
}