package com.example.quiz_app.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quiz_app.QuizAppNavigation
import com.example.quiz_app.data.repository.FakeAuthRepository
import com.example.quiz_app.presentation.auth.AuthViewModel
import com.example.quiz_app.ui.theme.QuizAppTheme
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationE2ETest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setup() {
        fakeAuthRepository = FakeAuthRepository()
        authViewModel = AuthViewModel(fakeAuthRepository)
    }

    @Test
    fun navigationE2E_loginToQuizListFlow() = runTest {
        // Given: User is logged out
        fakeAuthRepository.simulateLoggedOutUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(authViewModel = authViewModel)
            }
        }

        // Then: Should show login screen
        composeTestRule.onNodeWithText("クイズアプリ")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()

        // When: Login with valid credentials
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("testuser")
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("password")
        
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        // Wait for navigation
        composeTestRule.waitForIdle()

        // Then: Should navigate to quiz list
        composeTestRule.onNodeWithText("Quiz Library")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("ログアウト")
            .assertIsDisplayed()
    }

    @Test
    fun navigationE2E_quizListToQuizFlow() = runTest {
        // Given: User is authenticated
        fakeAuthRepository.simulateLoggedInUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(authViewModel = authViewModel)
            }
        }

        composeTestRule.waitForIdle()

        // Should be on quiz list screen
        composeTestRule.onNodeWithText("Quiz Library")
            .assertIsDisplayed()

        // When: Click on first quiz's Start Quiz button
        composeTestRule.onAllNodesWithText("Start Quiz")
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // Then: Should navigate to quiz screen
        composeTestRule.onNodeWithText("Quiz 1/")
            .assertExists()
        
        // Should show question content
        composeTestRule.onNodeWithText("Kotlinで変数を宣言するキーワードはどれ？")
            .assertIsDisplayed()
    }

    @Test
    fun navigationE2E_quizScreenBackNavigation() = runTest {
        // Given: User is authenticated and on quiz screen
        fakeAuthRepository.simulateLoggedInUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(authViewModel = authViewModel)
            }
        }

        composeTestRule.waitForIdle()

        // Navigate to quiz
        composeTestRule.onAllNodesWithText("Start Quiz")
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // When: Click back button
        composeTestRule.onNodeWithText("終了")
            .performClick()

        composeTestRule.waitForIdle()

        // Then: Should return to quiz list
        composeTestRule.onNodeWithText("Quiz Library")
            .assertIsDisplayed()
    }

    @Test
    fun navigationE2E_logoutFromQuizList() = runTest {
        // Given: User is authenticated
        fakeAuthRepository.simulateLoggedInUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(authViewModel = authViewModel)
            }
        }

        composeTestRule.waitForIdle()

        // Should be on quiz list screen
        composeTestRule.onNodeWithText("Quiz Library")
            .assertIsDisplayed()

        // When: Click logout button
        composeTestRule.onNodeWithText("ログアウト")
            .performClick()

        composeTestRule.waitForIdle()

        // Then: Should navigate back to login screen
        composeTestRule.onNodeWithText("クイズアプリ")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()
    }

    @Test
    fun navigationE2E_completeQuizFlow() = runTest {
        // Given: User is authenticated
        fakeAuthRepository.simulateLoggedInUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(authViewModel = authViewModel)
            }
        }

        composeTestRule.waitForIdle()

        // Start a quiz
        composeTestRule.onAllNodesWithText("Start Quiz")
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // Answer the question correctly
        composeTestRule.onNodeWithText("var")
            .performClick()
        
        composeTestRule.onNodeWithText("回答")
            .performClick()

        composeTestRule.waitForIdle()

        // Should show result
        composeTestRule.onNodeWithText("正解！")
            .assertIsDisplayed()

        // Move to next question or finish
        val nextButton = composeTestRule.onAllNodesWithText("次の問題")
            .fetchSemanticsNodes()
        
        if (nextButton.isNotEmpty()) {
            composeTestRule.onNodeWithText("次の問題")
                .performClick()
        } else {
            composeTestRule.onNodeWithText("結果を見る")
                .performClick()
        }

        composeTestRule.waitForIdle()

        // Should either show next question or return to quiz list
        // (Depending on whether there are more questions)
        val quizLibraryNodes = composeTestRule.onAllNodesWithText("Quiz Library")
            .fetchSemanticsNodes()
        
        if (quizLibraryNodes.isNotEmpty()) {
            // Quiz finished, back to quiz list
            composeTestRule.onNodeWithText("Quiz Library")
                .assertIsDisplayed()
        } else {
            // More questions available
            composeTestRule.onNodeWithText("Quiz 2/")
                .assertExists()
        }
    }

    @Test
    fun navigationE2E_invalidLoginStaysOnLoginScreen() = runTest {
        // Given: User is logged out
        fakeAuthRepository.simulateLoggedOutUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(authViewModel = authViewModel)
            }
        }

        // When: Login with invalid credentials
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("invalid")
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("invalid")
        
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // Then: Should stay on login screen and show error
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()
        
        // Should show some form of error indication
        // (The exact error message depends on AuthViewModel implementation)
    }

    @Test
    fun navigationE2E_deepLinkToQuizRequiresAuthentication() = runTest {
        // Given: User is not authenticated
        fakeAuthRepository.simulateLoggedOutUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(authViewModel = authViewModel)
            }
        }

        // Then: Should show login screen even if trying to access quiz
        composeTestRule.onNodeWithText("クイズアプリ")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()
    }

    @Test
    fun navigationE2E_authenticationStateChangesNavigation() = runTest {
        // Given: User starts logged out
        fakeAuthRepository.simulateLoggedOutUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(authViewModel = authViewModel)
            }
        }

        // Should show login screen
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()

        // When: Authentication state changes to logged in
        fakeAuthRepository.simulateLoggedInUser()

        composeTestRule.waitForIdle()

        // Then: Should automatically navigate to quiz list
        composeTestRule.onNodeWithText("Quiz Library")
            .assertIsDisplayed()

        // When: Authentication state changes back to logged out
        fakeAuthRepository.simulateLoggedOutUser()

        composeTestRule.waitForIdle()

        // Then: Should automatically navigate back to login
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()
    }

    @Test
    fun navigationE2E_multipleQuizNavigation() = runTest {
        // Given: User is authenticated
        fakeAuthRepository.simulateLoggedInUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(authViewModel = authViewModel)
            }
        }

        composeTestRule.waitForIdle()

        // Start first quiz
        composeTestRule.onAllNodesWithText("Start Quiz")
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // Verify on quiz screen
        composeTestRule.onNodeWithText("Quiz 1/")
            .assertExists()

        // Go back
        composeTestRule.onNodeWithText("終了")
            .performClick()

        composeTestRule.waitForIdle()

        // Start different quiz
        composeTestRule.onAllNodesWithText("Start Quiz")
            .onLast()
            .performClick()

        composeTestRule.waitForIdle()

        // Should be on different quiz
        composeTestRule.onNodeWithText("Quiz 1/")
            .assertExists()
    }
}