package com.example.quiz_app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quiz_app.data.repository.FakeAuthRepository
import com.example.quiz_app.domain.AuthState
import com.example.quiz_app.domain.User
import com.example.quiz_app.presentation.auth.AuthViewModel
import com.example.quiz_app.ui.theme.QuizAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityNavigationTest {

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
    fun navigation_authenticatedUserSeesQuizList() = runTest {
        // Arrange - Set up authenticated user
        fakeAuthRepository.simulateLoggedInUser()

        // Act
        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(
                    authViewModel = authViewModel
                )
            }
        }

        // Wait for navigation to complete
        composeTestRule.waitForIdle()

        // Assert - Should show QuizListScreen
        composeTestRule
            .onNodeWithText("Quiz Library")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("ログアウト")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_unauthenticatedUserSeesLogin() = runTest {
        // Arrange - Ensure user is logged out
        fakeAuthRepository.simulateLoggedOutUser()

        // Act
        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(
                    authViewModel = authViewModel
                )
            }
        }

        // Wait for navigation to complete
        composeTestRule.waitForIdle()

        // Assert - Should show LoginScreen
        composeTestRule
            .onNodeWithText("クイズアプリ")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("ログイン")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("ユーザー名")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_authStateChangeTriggersNavigation() = runTest {
        // Arrange - Start with unauthenticated user
        fakeAuthRepository.simulateLoggedOutUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(
                    authViewModel = authViewModel
                )
            }
        }

        // Assert initial state - should show login
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("ユーザー名")
            .assertIsDisplayed()

        // Act - Simulate login
        fakeAuthRepository.simulateLoggedInUser()

        // Wait for navigation to complete
        composeTestRule.waitForIdle()

        // Assert - Should now show QuizListScreen
        composeTestRule
            .onNodeWithText("Quiz Library")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_logoutNavigatesToLogin() = runTest {
        // Arrange - Start with authenticated user
        fakeAuthRepository.simulateLoggedInUser()

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(
                    authViewModel = authViewModel
                )
            }
        }

        // Assert initial state - should show quiz list
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("Quiz Library")
            .assertIsDisplayed()

        // Act - Simulate logout
        fakeAuthRepository.simulateLoggedOutUser()

        // Wait for navigation to complete
        composeTestRule.waitForIdle()

        // Assert - Should now show LoginScreen
        composeTestRule
            .onNodeWithText("ユーザー名")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("パスワード")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_errorStateNavigatesToLogin() = runTest {
        // Arrange - Set auth state to error
        val errorMessage = "Authentication error occurred"
        // Create a custom auth state flow for this test
        fakeAuthRepository.getAuthStateFlow().value = AuthState.Error(errorMessage)

        composeTestRule.setContent {
            QuizAppTheme {
                QuizAppNavigation(
                    authViewModel = authViewModel
                )
            }
        }

        // Wait for navigation to complete
        composeTestRule.waitForIdle()

        // Assert - Should show LoginScreen on error
        composeTestRule
            .onNodeWithText("ユーザー名")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("パスワード")
            .assertIsDisplayed()
    }
}