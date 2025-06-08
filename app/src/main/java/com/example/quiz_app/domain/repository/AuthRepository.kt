package com.example.quiz_app.domain.repository

import com.example.quiz_app.domain.AuthState
import com.example.quiz_app.domain.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>
    
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun refreshToken(): Result<Unit>
}