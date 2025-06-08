package com.example.quiz_app.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `User data class should create instance with all properties`() {
        val user = User(
            id = "user123",
            username = "johndoe",
            email = "john@example.com",
            displayName = "John Doe",
            avatarUrl = "https://example.com/avatar.jpg",
            createdAt = "2024-01-15T10:30:00Z",
            updatedAt = "2024-01-20T15:45:00Z"
        )

        assertEquals("user123", user.id)
        assertEquals("johndoe", user.username)
        assertEquals("john@example.com", user.email)
        assertEquals("John Doe", user.displayName)
        assertEquals("https://example.com/avatar.jpg", user.avatarUrl)
        assertEquals("2024-01-15T10:30:00Z", user.createdAt)
        assertEquals("2024-01-20T15:45:00Z", user.updatedAt)
    }

    @Test
    fun `User data class should handle null avatarUrl`() {
        val user = User(
            id = "user456",
            username = "jane",
            email = "jane@example.com",
            displayName = "Jane Smith",
            avatarUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        assertNull(user.avatarUrl)
    }

    @Test
    fun `User data class should support equality comparison`() {
        val user1 = User(
            id = "123",
            username = "test",
            email = "test@example.com",
            displayName = "Test User",
            avatarUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        val user2 = User(
            id = "123",
            username = "test",
            email = "test@example.com",
            displayName = "Test User",
            avatarUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        val user3 = User(
            id = "456",
            username = "different",
            email = "different@example.com",
            displayName = "Different User",
            avatarUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
    }

    @Test
    fun `User data class should support copy functionality`() {
        val originalUser = User(
            id = "123",
            username = "original",
            email = "original@example.com",
            displayName = "Original User",
            avatarUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        val updatedUser = originalUser.copy(
            displayName = "Updated User",
            updatedAt = "2024-01-02T00:00:00Z"
        )

        assertEquals("123", updatedUser.id)
        assertEquals("original", updatedUser.username)
        assertEquals("original@example.com", updatedUser.email)
        assertEquals("Updated User", updatedUser.displayName)
        assertEquals("2024-01-02T00:00:00Z", updatedUser.updatedAt)
    }
}