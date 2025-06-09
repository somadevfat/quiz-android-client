package com.example.quiz_app.presentation.auth

import app.cash.turbine.test
import com.example.quiz_app.data.repository.FakeAuthRepository
import com.example.quiz_app.domain.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var authViewModel: AuthViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        authViewModel = AuthViewModel(fakeAuthRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial login UI state should be empty`() = runTest {
        authViewModel.loginUiState.test {
            val initialState = awaitItem()
            
            assertEquals("", initialState.username)
            assertEquals("", initialState.password)
            assertFalse(initialState.isLoading)
            assertNull(initialState.errorMessage)
            assertFalse(initialState.isLoginSuccessful)
        }
    }

    @Test
    fun `updateLoginUsername should update login state`() = runTest {
        authViewModel.updateLoginUsername("testuser")
        
        authViewModel.loginUiState.test {
            val state = awaitItem()
            assertEquals("testuser", state.username)
        }
    }

    @Test
    fun `updateLoginPassword should update login state`() = runTest {
        authViewModel.updateLoginPassword("password123")
        
        authViewModel.loginUiState.test {
            val state = awaitItem()
            assertEquals("password123", state.password)
        }
    }

    @Test
    fun `login with empty credentials should show validation error`() = runTest {
        authViewModel.login()
        
        authViewModel.loginUiState.test {
            val state = awaitItem()
            assertEquals("ユーザー名とパスワードを入力してください", state.errorMessage)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `login with valid credentials should succeed`() = runTest {
        authViewModel.updateLoginUsername("testuser")
        authViewModel.updateLoginPassword("password")
        
        authViewModel.loginUiState.test {
            // Skip initial state
            awaitItem()
            
            authViewModel.login()
            
            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.errorMessage)
            
            // Wait for auth to complete
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertNull(successState.errorMessage)
            assertTrue(successState.isLoginSuccessful)
        }
    }

    @Test
    fun `login with invalid credentials should show error`() = runTest {
        authViewModel.updateLoginUsername("wronguser")
        authViewModel.updateLoginPassword("wrongpassword")
        
        authViewModel.loginUiState.test {
            // Skip initial state
            awaitItem()
            
            authViewModel.login()
            
            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            // Wait for auth to complete
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("ユーザー名またはパスワードが間違っています", errorState.errorMessage)
        }
    }

    @Test
    fun `register with valid data should succeed`() = runTest {
        authViewModel.updateRegisterUsername("newuser")
        authViewModel.updateRegisterEmail("new@example.com")
        authViewModel.updateRegisterPassword("password123")
        authViewModel.updateRegisterConfirmPassword("password123")
        
        authViewModel.registerUiState.test {
            // Skip initial state
            awaitItem()
            
            authViewModel.register()
            
            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            // Wait for registration to complete
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertNull(successState.errorMessage)
            assertTrue(successState.isRegisterSuccessful)
        }
    }

    @Test
    fun `register with mismatched passwords should show error`() = runTest {
        authViewModel.updateRegisterUsername("user")
        authViewModel.updateRegisterEmail("user@example.com")
        authViewModel.updateRegisterPassword("password123")
        authViewModel.updateRegisterConfirmPassword("different")
        
        authViewModel.register()
        
        authViewModel.registerUiState.test {
            val state = awaitItem()
            assertEquals("パスワードが一致しません", state.errorMessage)
        }
    }

    @Test
    fun `register with short password should show error`() = runTest {
        authViewModel.updateRegisterUsername("user")
        authViewModel.updateRegisterEmail("user@example.com")
        authViewModel.updateRegisterPassword("123")
        authViewModel.updateRegisterConfirmPassword("123")
        
        authViewModel.register()
        
        authViewModel.registerUiState.test {
            val state = awaitItem()
            assertEquals("パスワードは6文字以上で入力してください", state.errorMessage)
        }
    }

    @Test
    fun `logout should reset UI states`() = runTest {
        // First login
        authViewModel.updateLoginUsername("testuser")
        authViewModel.updateLoginPassword("password")
        authViewModel.login()
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then logout
        authViewModel.logout()
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Check login state is reset
        authViewModel.loginUiState.test {
            val loginState = awaitItem()
            assertEquals("", loginState.username)
            assertEquals("", loginState.password)
            assertFalse(loginState.isLoading)
            assertNull(loginState.errorMessage)
            assertFalse(loginState.isLoginSuccessful)
        }
    }

    @Test
    fun `auth state should reflect repository state`() = runTest {
        // Setup login credentials first
        authViewModel.updateLoginUsername("testuser")
        authViewModel.updateLoginPassword("password")
        
        authViewModel.authState.test {
            // Initial state should be unauthenticated
            var initialState = awaitItem()
            if (initialState is AuthState.Loading) {
                initialState = awaitItem() // Consume Unauthenticated if Loading was first
            }
            assertTrue(initialState is AuthState.Unauthenticated)
            
            // Perform login
            authViewModel.login()
            
            // The state should first transition to Loading
            val loadingState = awaitItem()
            assertTrue(loadingState is AuthState.Loading)

            testDispatcher.scheduler.advanceUntilIdle()
            
            // Wait for authenticated state
            val authenticatedState = awaitItem()
            assertTrue(authenticatedState is AuthState.Authenticated)
            assertEquals("testuser", (authenticatedState as AuthState.Authenticated).user.username)
        }
    }

    @Test
    fun `getCurrentUser should return user when authenticated`() = runTest {
        authViewModel.updateLoginUsername("testuser")
        authViewModel.updateLoginPassword("password")
        authViewModel.login()
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        val user = authViewModel.getCurrentUser()
        assertNotNull(user)
        assertEquals("testuser", user?.username)
    }

    @Test
    fun `getCurrentUser should return null when not authenticated`() = runTest {
        val user = authViewModel.getCurrentUser()
        assertNull(user)
    }

    @Test
    fun `clearLoginError should clear error message`() = runTest {
        authViewModel.login() // This will set validation error
        
        authViewModel.clearLoginError()
        
        authViewModel.loginUiState.test {
            val state = awaitItem()
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `clearRegisterError should clear error message`() = runTest {
        authViewModel.register() // This will set validation error
        
        authViewModel.clearRegisterError()
        
        authViewModel.registerUiState.test {
            val state = awaitItem()
            assertNull(state.errorMessage)
        }
    }
}