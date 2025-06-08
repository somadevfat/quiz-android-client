package com.example.quiz_app.presentation.quiz_list

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quiz_app.data.repository.FakeQuizRepository
import com.example.quiz_app.ui.theme.QuizAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun quizListScreen_displaysTitle() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(FakeQuizRepository())
                )
            }
        }

        composeTestRule
            .onNodeWithText("Quiz Library")
            .assertIsDisplayed()
    }

    @Test
    fun quizListScreen_displaysQuizzesFromFakeRepository() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(FakeQuizRepository())
                )
            }
        }

        // Wait for loading to complete
        composeTestRule.waitForIdle()

        // Verify fake repository data is displayed
        composeTestRule
            .onNodeWithText("Android Development Fundamentals")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Kotlin Advanced Features")
            .assertIsDisplayed()
    }

    @Test
    fun quizCard_startQuizButtonIsClickable() {
        composeTestRule.setContent {
            QuizAppTheme {
                QuizListScreen(
                    viewModel = QuizListViewModel(FakeQuizRepository())
                )
            }
        }

        composeTestRule.waitForIdle()

        // Find and verify Start Quiz button exists and is clickable
        composeTestRule
            .onAllNodesWithText("Start Quiz")
            .onFirst()
            .assertIsDisplayed()
            .assertHasClickAction()
    }
}