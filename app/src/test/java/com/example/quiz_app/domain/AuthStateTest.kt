package com.example.quiz_app.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AuthStateTest {

    @Test
    fun `AuthState sealed class should have correct structure`() {
        // Test Loading state
        val loadingState = AuthState.Loading
        assertTrue(loadingState is AuthState.Loading)
        
        // Test Unauthenticated state
        val unauthenticatedState = AuthState.Unauthenticated
        assertTrue(unauthenticatedState is AuthState.Unauthenticated)
        
        // Test Authenticated state
        val testUser = User(
            id = "1",
            username = "testuser",
            email = "test@example.com",
            displayName = "Test User",
            avatarUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
        val authenticatedState = AuthState.Authenticated(testUser)
        assertTrue(authenticatedState is AuthState.Authenticated)
        assertEquals(testUser, authenticatedState.user)
        
        // Test Error state
        val errorMessage = "Authentication failed"
        val errorState = AuthState.Error(errorMessage)
        assertTrue(errorState is AuthState.Error)
        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun `AuthState should be comparable by type`() {
        val loading1 = AuthState.Loading
        val loading2 = AuthState.Loading
        val unauthenticated = AuthState.Unauthenticated
        
        assertEquals(loading1, loading2)
        assertNotEquals(loading1, unauthenticated)
    }
}