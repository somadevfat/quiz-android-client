package com.example.quiz_app.presentation.quiz

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quiz_app.domain.Question
import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.QuizDifficulty
import com.example.quiz_app.domain.repository.QuizRepository
import com.example.quiz_app.ui.theme.QuizAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockRepository: QuizRepository
    private lateinit var viewModel: QuizViewModel

    private val sampleQuestions = listOf(
        Question(
            id = "q1",
            questionText = "What is 2 + 2?",
            options = listOf("3", "4", "5", "6"),
            correctAnswerIndex = 1,
            explanation = "2 + 2 equals 4"
        ),
        Question(
            id = "q2",
            questionText = "What is the capital of Japan?",
            options = listOf("Seoul", "Tokyo", "Beijing", "Bangkok"),
            correctAnswerIndex = 1,
            explanation = "Tokyo is the capital of Japan"
        )
    )

    @Before
    fun setup() {
        mockRepository = object : QuizRepository {
            override fun getQuizzes(): Flow<List<Quiz>> = flowOf(emptyList())
            override suspend fun getQuizById(id: String): Result<Quiz> = Result.success(Quiz(
                id = "1", title = "Test Quiz", description = "Test", difficulty = QuizDifficulty.BEGINNER,
                categoryId = "test", categoryName = "Test", questionCount = 2, timeLimit = 10,
                imageUrl = null, createdAt = "", updatedAt = ""
            ))
            override suspend fun searchQuizzes(query: String): Result<List<Quiz>> = Result.success(emptyList())
            override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> = Result.success(emptyList())
            override suspend fun getQuizDetail(quizId: String): Result<List<Question>> = Result.success(sampleQuestions)
        }
        
        viewModel = QuizViewModel(mockRepository)
    }

    @Test
    fun quizScreen_displaysLoadingState() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = {},
                    onQuizFinished = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        // Should show loading indicator initially
        composeTestRule.onNodeWithTag("loadingIndicator", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun quizScreen_displaysQuestionAfterLoading() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = {},
                    onQuizFinished = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        // Wait for loading to complete
        composeTestRule.waitForIdle()

        // Should display question text
        composeTestRule.onNodeWithText("What is 2 + 2?")
            .assertIsDisplayed()

        // Should display all options
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
        composeTestRule.onNodeWithText("4").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        composeTestRule.onNodeWithText("6").assertIsDisplayed()

        // Should display progress indicator
        composeTestRule.onNodeWithText("Quiz 1/2")
            .assertIsDisplayed()
    }

    @Test
    fun quizScreen_allowsAnswerSelection() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = {},
                    onQuizFinished = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // Click on option "4"
        composeTestRule.onNodeWithText("4")
            .performClick()

        // Answer button should be enabled
        composeTestRule.onNodeWithText("回答")
            .assertIsEnabled()
    }

    @Test
    fun quizScreen_submitAnswerShowsResult() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = {},
                    onQuizFinished = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // Select correct answer and submit
        composeTestRule.onNodeWithText("4").performClick()
        composeTestRule.onNodeWithText("回答").performClick()

        composeTestRule.waitForIdle()

        // Should show result
        composeTestRule.onNodeWithText("正解！")
            .assertIsDisplayed()

        // Should show explanation
        composeTestRule.onNodeWithText("2 + 2 equals 4")
            .assertIsDisplayed()

        // Should show score
        composeTestRule.onNodeWithText("現在のスコア: 1/2")
            .assertIsDisplayed()

        // Should show next question button
        composeTestRule.onNodeWithText("次の問題")
            .assertIsDisplayed()
    }

    @Test
    fun quizScreen_incorrectAnswerShowsCorrectResult() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = {},
                    onQuizFinished = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // Select incorrect answer and submit
        composeTestRule.onNodeWithText("3").performClick()
        composeTestRule.onNodeWithText("回答").performClick()

        composeTestRule.waitForIdle()

        // Should show incorrect result
        composeTestRule.onNodeWithText("不正解")
            .assertIsDisplayed()

        // Should show score (still 0)
        composeTestRule.onNodeWithText("現在のスコア: 0/2")
            .assertIsDisplayed()
    }

    @Test
    fun quizScreen_navigatesToNextQuestion() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = {},
                    onQuizFinished = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // Answer first question
        composeTestRule.onNodeWithText("4").performClick()
        composeTestRule.onNodeWithText("回答").performClick()

        composeTestRule.waitForIdle()

        // Click next question
        composeTestRule.onNodeWithText("次の問題").performClick()

        composeTestRule.waitForIdle()

        // Should display second question
        composeTestRule.onNodeWithText("What is the capital of Japan?")
            .assertIsDisplayed()

        // Should update progress
        composeTestRule.onNodeWithText("Quiz 2/2")
            .assertIsDisplayed()

        // Should display new options
        composeTestRule.onNodeWithText("Seoul").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tokyo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Beijing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bangkok").assertIsDisplayed()
    }

    @Test
    fun quizScreen_finishQuizOnLastQuestion() {
        var quizFinishedCalled = false
        var finalScore = 0
        var totalQuestions = 0

        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = {},
                    onQuizFinished = { score, total ->
                        quizFinishedCalled = true
                        finalScore = score
                        totalQuestions = total
                    },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // Answer first question correctly
        composeTestRule.onNodeWithText("4").performClick()
        composeTestRule.onNodeWithText("回答").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("次の問題").performClick()
        composeTestRule.waitForIdle()

        // Answer second question correctly
        composeTestRule.onNodeWithText("Tokyo").performClick()
        composeTestRule.onNodeWithText("回答").performClick()
        composeTestRule.waitForIdle()

        // Should show "結果を見る" on last question
        composeTestRule.onNodeWithText("結果を見る")
            .assertIsDisplayed()

        // Click to finish quiz
        composeTestRule.onNodeWithText("結果を見る").performClick()

        composeTestRule.waitForIdle()

        // Should call onQuizFinished with correct parameters
        assert(quizFinishedCalled)
        assert(finalScore == 2)
        assert(totalQuestions == 2)
    }

    @Test
    fun quizScreen_backButtonWorks() {
        var backButtonClicked = false

        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = { backButtonClicked = true },
                    onQuizFinished = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // Click back button
        composeTestRule.onNodeWithText("終了")
            .performClick()

        // Should call onNavigateBack
        assert(backButtonClicked)
    }

    @Test
    fun quizScreen_answersNotSelectableAfterSubmission() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = {},
                    onQuizFinished = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // Submit an answer
        composeTestRule.onNodeWithText("4").performClick()
        composeTestRule.onNodeWithText("回答").performClick()

        composeTestRule.waitForIdle()

        // Try to click another option - should not change selection
        composeTestRule.onNodeWithText("3").performClick()

        // The UI should still show the result, not allow new selections
        composeTestRule.onNodeWithText("正解！")
            .assertIsDisplayed()
    }

    @Test
    fun quizScreen_showsProgressBarCorrectly() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "1",
                    onNavigateBack = {},
                    onQuizFinished = { _, _ -> },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // Should show progress indicator
        composeTestRule.onNodeWithText("Quiz 1/2")
            .assertIsDisplayed()

        // Answer first question and move to next
        composeTestRule.onNodeWithText("4").performClick()
        composeTestRule.onNodeWithText("回答").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("次の問題").performClick()
        composeTestRule.waitForIdle()

        // Should update progress
        composeTestRule.onNodeWithText("Quiz 2/2")
            .assertIsDisplayed()
    }

    @Test
    fun quizScreen_displaysErrorState() {
        val errorRepository = object : QuizRepository {
            override fun getQuizzes(): Flow<List<Quiz>> = flowOf(emptyList())
            override suspend fun getQuizById(id: String): Result<Quiz> = Result.failure(RuntimeException("Network error"))
            override suspend fun searchQuizzes(query: String): Result<List<Quiz>> = Result.success(emptyList())
            override suspend fun getQuizzesByCategory(categoryId: String): Result<List<Quiz>> = Result.success(emptyList())
            override suspend fun getQuizDetail(quizId: String): Result<List<Question>> = Result.failure(RuntimeException("Quiz not found"))
        }

        val errorViewModel = QuizViewModel(errorRepository)

        composeTestRule.setContent {
            QuizAppTheme {
                QuizScreen(
                    quizId = "invalid",
                    onNavigateBack = {},
                    onQuizFinished = { _, _ -> },
                    viewModel = errorViewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // Should display error message
        composeTestRule.onNodeWithText("エラーが発生しました")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Quiz not found")
            .assertIsDisplayed()

        // Should show back button
        composeTestRule.onNodeWithText("戻る")
            .assertIsDisplayed()
    }
}