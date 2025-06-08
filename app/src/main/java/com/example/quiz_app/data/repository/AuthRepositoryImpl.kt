package com.example.quiz_app.data.repository

import com.example.quiz_app.data.local.TokenManager
import com.example.quiz_app.data.remote.AuthApiService
import com.example.quiz_app.data.remote.dto.LoginRequest
import com.example.quiz_app.data.remote.dto.RefreshTokenRequest
import com.example.quiz_app.data.remote.dto.RegisterRequest
import com.example.quiz_app.data.remote.dto.toDomain
import com.example.quiz_app.domain.AuthState
import com.example.quiz_app.domain.User
import com.example.quiz_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: Flow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentAuthState()
    }

    private fun checkCurrentAuthState() {
        val accessToken = tokenManager.getAccessToken()
        if (accessToken != null) {
            _authState.value = AuthState.Loading
            // TODO: Verify token validity with API
            // For now, assume valid token means authenticated
            _authState.value = AuthState.Unauthenticated // Temporary until user fetch
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            _authState.value = AuthState.Loading
            
            val response = authApiService.login(LoginRequest(username, password))
            
            // Save tokens securely
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
            
            val user = response.user.toDomain()
            _authState.value = AuthState.Authenticated(user)
            
            Result.success(user)
        } catch (exception: Exception) {
            val errorMessage = exception.message ?: "ログインに失敗しました"
            _authState.value = AuthState.Error(errorMessage)
            Result.failure(exception)
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        return try {
            _authState.value = AuthState.Loading
            
            val response = authApiService.register(RegisterRequest(username, email, password))
            
            // Save tokens securely
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
            
            val user = response.user.toDomain()
            _authState.value = AuthState.Authenticated(user)
            
            Result.success(user)
        } catch (exception: Exception) {
            val errorMessage = exception.message ?: "ユーザー登録に失敗しました"
            _authState.value = AuthState.Error(errorMessage)
            Result.failure(exception)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val accessToken = tokenManager.getAccessToken()
            if (accessToken != null) {
                authApiService.logout("Bearer $accessToken")
            }
            
            // Clear tokens regardless of API call result
            tokenManager.clearTokens()
            _authState.value = AuthState.Unauthenticated
            
            Result.success(Unit)
        } catch (exception: Exception) {
            // Even if API call fails, clear local tokens
            tokenManager.clearTokens()
            _authState.value = AuthState.Unauthenticated
            Result.success(Unit) // Always succeed for logout
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val accessToken = tokenManager.getAccessToken() ?: return null
            val userDto = authApiService.getCurrentUser("Bearer $accessToken")
            val user = userDto.toDomain()
            _authState.value = AuthState.Authenticated(user)
            user
        } catch (exception: Exception) {
            _authState.value = AuthState.Error(exception.message ?: "ユーザー情報の取得に失敗しました")
            null
        }
    }

    override suspend fun refreshToken(): Result<Unit> {
        return try {
            val refreshToken = tokenManager.getRefreshToken() ?: return Result.failure(
                Exception("リフレッシュトークンが見つかりません")
            )
            
            val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
            
            Result.success(Unit)
        } catch (exception: Exception) {
            // If refresh fails, clear tokens and set unauthenticated
            tokenManager.clearTokens()
            _authState.value = AuthState.Unauthenticated
            Result.failure(exception)
        }
    }
}