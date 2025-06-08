package com.example.quiz_app.data.repository

import app.cash.turbine.test
import com.example.quiz_app.domain.AuthState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FakeAuthRepositoryTest {

    private lateinit var repository: FakeAuthRepository

    @BeforeEach
    fun setup() {
        repository = FakeAuthRepository()
    }

    @Test
    fun `initial auth state should be unauthenticated`() = runTest {
        repository.authState.test {
            val initialState = awaitItem()
            assertTrue(initialState is AuthState.Unauthenticated)
        }
    }

    @Test
    fun `login with valid credentials should return success`() = runTest {
        val result = repository.login("testuser", "password")
        
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("testuser", user!!.username)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun `login with invalid credentials should return failure`() = runTest {
        val result = repository.login("wronguser", "wrongpassword")
        
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertEquals("ユーザー名またはパスワードが間違っています", exception!!.message)
    }

    @Test
    fun `login with empty fields should return validation error`() = runTest {
        val result = repository.login("", "")
        
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertEquals("ユーザー名とパスワードを入力してください", exception!!.message)
    }

    @Test
    fun `login should update auth state to authenticated on success`() = runTest {
        repository.authState.test {
            // Skip initial state
            awaitItem()
            
            repository.login("testuser", "password")
            
            // Skip loading state
            val loadingState = awaitItem()
            assertTrue(loadingState is AuthState.Loading)
            
            // Check authenticated state
            val authenticatedState = awaitItem()
            assertTrue(authenticatedState is AuthState.Authenticated)
            assertEquals("testuser", (authenticatedState as AuthState.Authenticated).user.username)
        }
    }

    @Test
    fun `login should update auth state to error on failure`() = runTest {
        repository.authState.test {
            // Skip initial state
            awaitItem()
            
            repository.login("wronguser", "wrongpassword")
            
            // Skip loading state
            val loadingState = awaitItem()
            assertTrue(loadingState is AuthState.Loading)
            
            // Check error state
            val errorState = awaitItem()
            assertTrue(errorState is AuthState.Error)
            assertEquals("ユーザー名またはパスワードが間違っています", (errorState as AuthState.Error).message)
        }
    }

    @Test
    fun `register with valid data should return success`() = runTest {
        val result = repository.register("newuser", "new@example.com", "password123")
        
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("newuser", user!!.username)
        assertEquals("new@example.com", user.email)
    }

    @Test
    fun `register with existing username should return failure`() = runTest {
        val result = repository.register("testuser", "test2@example.com", "password123")
        
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertEquals("このユーザー名は既に使用されています", exception!!.message)
    }

    @Test
    fun `logout should update auth state to unauthenticated`() = runTest {
        // First login to set authenticated state
        repository.login("testuser", "password")
        
        // Wait for login to complete
        val loginResult = repository.getCurrentUser()
        assertNotNull(loginResult)
        
        // Now test logout
        val logoutResult = repository.logout()
        assertTrue(logoutResult.isSuccess)
        
        // Verify auth state is updated
        repository.authState.test {
            val currentState = awaitItem()
            assertTrue(currentState is AuthState.Unauthenticated)
        }
    }

    @Test
    fun `getCurrentUser should return user when authenticated`() = runTest {
        // First login
        repository.login("testuser", "password")
        
        val user = repository.getCurrentUser()
        assertNotNull(user)
        assertEquals("testuser", user!!.username)
    }

    @Test
    fun `getCurrentUser should return null when not authenticated`() = runTest {
        val user = repository.getCurrentUser()
        assertNull(user)
    }

    @Test
    fun `refreshToken should succeed when user is authenticated`() = runTest {
        // First login
        repository.login("testuser", "password")
        
        val result = repository.refreshToken()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `refreshToken should fail when user is not authenticated`() = runTest {
        val result = repository.refreshToken()
        assertTrue(result.isFailure)
    }

    @Test
    fun `test helper methods should work correctly`() = runTest {
        repository.authState.test {
            // Skip initial state
            awaitItem()
            
            // Test simulateLoggedInUser
            repository.simulateLoggedInUser()
            val authenticatedState = awaitItem()
            assertTrue(authenticatedState is AuthState.Authenticated)
            
            // Test simulateLoggedOutUser
            repository.simulateLoggedOutUser()
            val unauthenticatedState = awaitItem()
            assertTrue(unauthenticatedState is AuthState.Unauthenticated)
        }
    }
}