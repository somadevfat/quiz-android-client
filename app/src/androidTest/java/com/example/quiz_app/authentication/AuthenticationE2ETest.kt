package com.example.quiz_app.authentication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quiz_app.data.repository.FakeAuthRepository
import com.example.quiz_app.domain.AuthState
import com.example.quiz_app.presentation.auth.AuthViewModel
import com.example.quiz_app.presentation.auth.LoginScreen
import com.example.quiz_app.ui.theme.QuizAppTheme
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthenticationE2ETest {

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
    fun authE2E_successfulLoginFlow() = runTest {
        var navigationTriggered = false

        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = { navigationTriggered = true },
                    viewModel = authViewModel
                )
            }
        }

        // Verify initial login screen state
        composeTestRule.onNodeWithText("クイズアプリ")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("パスワード")
            .assertIsDisplayed()

        // Enter valid credentials
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("testuser")
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("password")

        // Submit login
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify navigation was triggered
        assert(navigationTriggered) { "Navigation should have been triggered after successful login" }
    }

    @Test
    fun authE2E_failedLoginFlow() = runTest {
        var navigationTriggered = false

        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = { navigationTriggered = true },
                    viewModel = authViewModel
                )
            }
        }

        // Enter invalid credentials
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("invalid")
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("invalid")

        // Submit login
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // Should stay on login screen
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()

        // Navigation should not have been triggered
        assert(!navigationTriggered) { "Navigation should not have been triggered after failed login" }
    }

    @Test
    fun authE2E_emptyCredentialsValidation() = runTest {
        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = {},
                    viewModel = authViewModel
                )
            }
        }

        // Try to login without entering credentials
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // Should show validation errors or disable button
        // The login button should remain on screen
        composeTestRule.onNodeWithText("ログイン")
            .assertIsDisplayed()
    }

    @Test
    fun authE2E_usernameFieldValidation() = runTest {
        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = {},
                    viewModel = authViewModel
                )
            }
        }

        // Test username field
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("test")
        
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextClearance()
        
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("validuser")

        // Verify text was entered correctly
        composeTestRule.onNodeWithText("ユーザー名")
            .assertExists()
    }

    @Test
    fun authE2E_passwordFieldSecurity() = runTest {
        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = {},
                    viewModel = authViewModel
                )
            }
        }

        // Enter password
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("secretpassword")

        // Password field should exist but text should be masked
        composeTestRule.onNodeWithText("パスワード")
            .assertExists()
        
        // The actual password text should not be visible
        composeTestRule.onNodeWithText("secretpassword", useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun authE2E_loadingStateDisplayed() = runTest {
        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = {},
                    viewModel = authViewModel
                )
            }
        }

        // Enter credentials
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("testuser")
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("password")

        // Click login - this should trigger loading state
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        // Note: In a real scenario, we might see a loading indicator
        // For FakeAuthRepository, this happens very quickly
        composeTestRule.waitForIdle()
    }

    @Test
    fun authE2E_logoutFunctionality() = runTest {
        // Given: User is logged in
        fakeAuthRepository.simulateLoggedInUser()

        // When: Logout is called
        authViewModel.logout()

        // Wait for state change
        composeTestRule.waitForIdle()

        // Then: Auth state should be unauthenticated
        val currentState = authViewModel.authState.value
        assert(currentState is AuthState.Unauthenticated) { 
            "Expected Unauthenticated state, but got $currentState" 
        }
    }

    @Test
    fun authE2E_authStateTransitions() = runTest {
        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = {},
                    viewModel = authViewModel
                )
            }
        }

        // Initial state should be unauthenticated
        var currentState = authViewModel.authState.value
        assert(currentState is AuthState.Unauthenticated) { 
            "Initial state should be Unauthenticated, but got $currentState" 
        }

        // Enter credentials and login
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("testuser")
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("password")
        
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // State should change to authenticated
        currentState = authViewModel.authState.value
        assert(currentState is AuthState.Authenticated) { 
            "After successful login, state should be Authenticated, but got $currentState" 
        }

        // Logout
        authViewModel.logout()
        composeTestRule.waitForIdle()

        // State should change back to unauthenticated
        currentState = authViewModel.authState.value
        assert(currentState is AuthState.Unauthenticated) { 
            "After logout, state should be Unauthenticated, but got $currentState" 
        }
    }

    @Test
    fun authE2E_errorHandling() = runTest {
        // Set up repository to simulate error
        fakeAuthRepository.simulateNetworkError()

        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = {},
                    viewModel = authViewModel
                )
            }
        }

        // Try to login
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("testuser")
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("password")
        
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // Should handle error gracefully and stay on login screen
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()
        
        // Error state should be set
        val currentState = authViewModel.authState.value
        assert(currentState is AuthState.Error) { 
            "After network error, state should be Error, but got $currentState" 
        }
    }

    @Test
    fun authE2E_multipleLoginAttempts() = runTest {
        var navigationCount = 0

        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = { navigationCount++ },
                    viewModel = authViewModel
                )
            }
        }

        // First attempt - wrong credentials
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("wrong")
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("wrong")
        
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // Should stay on login screen
        assert(navigationCount == 0) { "Navigation should not occur with wrong credentials" }

        // Clear fields and try correct credentials
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextClearance()
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextClearance()
        
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("testuser")
        
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("password")
        
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // Should navigate
        assert(navigationCount == 1) { "Navigation should occur with correct credentials" }
    }

    @Test
    fun authE2E_formValidationAndSubmission() = runTest {
        composeTestRule.setContent {
            QuizAppTheme {
                LoginScreen(
                    onNavigateToQuizList = {},
                    viewModel = authViewModel
                )
            }
        }

        // Test that all required fields must be filled
        composeTestRule.onNodeWithText("ユーザー名")
            .performTextInput("testuser")
        
        // Try to login with only username filled
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // Should not navigate (password is empty)
        composeTestRule.onNodeWithText("ユーザー名")
            .assertIsDisplayed()

        // Fill in password
        composeTestRule.onNodeWithText("パスワード")
            .performTextInput("password")
        
        // Now login should work
        composeTestRule.onNodeWithText("ログイン")
            .performClick()

        composeTestRule.waitForIdle()

        // Should be successful
        val currentState = authViewModel.authState.value
        assert(currentState is AuthState.Authenticated) { 
            "With complete credentials, should be authenticated, but got $currentState" 
        }
    }
}