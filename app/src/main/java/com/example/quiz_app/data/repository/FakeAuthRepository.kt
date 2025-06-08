package com.example.quiz_app.data.repository

import com.example.quiz_app.domain.AuthState
import com.example.quiz_app.domain.User
import com.example.quiz_app.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAuthRepository @Inject constructor() : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: Flow<AuthState> = _authState.asStateFlow()

    private val fakeUsers = mutableMapOf<String, User>()
    private var currentUser: User? = null

    // Pre-populated test user
    init {
        val testUser = User(
            id = "test-user-1",
            username = "testuser",
            email = "test@example.com",
            displayName = "Test User",
            avatarUrl = null,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
        fakeUsers["testuser"] = testUser
    }

    override suspend fun login(username: String, password: String): Result<User> {
        // Simulate network delay
        delay(1000)
        
        return try {
            _authState.value = AuthState.Loading
            
            // Simulate authentication logic
            when {
                username.isEmpty() || password.isEmpty() -> {
                    _authState.value = AuthState.Error("ユーザー名とパスワードを入力してください")
                    Result.failure(Exception("ユーザー名とパスワードを入力してください"))
                }
                username == "testuser" && password == "password" -> {
                    val user = fakeUsers[username]!!
                    currentUser = user
                    _authState.value = AuthState.Authenticated(user)
                    Result.success(user)
                }
                username == "error" -> {
                    _authState.value = AuthState.Error("ネットワークエラーが発生しました")
                    Result.failure(Exception("ネットワークエラーが発生しました"))
                }
                else -> {
                    _authState.value = AuthState.Error("ユーザー名またはパスワードが間違っています")
                    Result.failure(Exception("ユーザー名またはパスワードが間違っています"))
                }
            }
        } catch (exception: Exception) {
            _authState.value = AuthState.Error(exception.message ?: "ログインに失敗しました")
            Result.failure(exception)
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        // Simulate network delay
        delay(1500)
        
        return try {
            _authState.value = AuthState.Loading
            
            when {
                username.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                    _authState.value = AuthState.Error("すべての項目を入力してください")
                    Result.failure(Exception("すべての項目を入力してください"))
                }
                fakeUsers.containsKey(username) -> {
                    _authState.value = AuthState.Error("このユーザー名は既に使用されています")
                    Result.failure(Exception("このユーザー名は既に使用されています"))
                }
                username == "error" -> {
                    _authState.value = AuthState.Error("サーバーエラーが発生しました")
                    Result.failure(Exception("サーバーエラーが発生しました"))
                }
                else -> {
                    val newUser = User(
                        id = "user-${System.currentTimeMillis()}",
                        username = username,
                        email = email,
                        displayName = username,
                        avatarUrl = null,
                        createdAt = "2024-01-01T00:00:00Z",
                        updatedAt = "2024-01-01T00:00:00Z"
                    )
                    
                    fakeUsers[username] = newUser
                    currentUser = newUser
                    _authState.value = AuthState.Authenticated(newUser)
                    Result.success(newUser)
                }
            }
        } catch (exception: Exception) {
            _authState.value = AuthState.Error(exception.message ?: "ユーザー登録に失敗しました")
            Result.failure(exception)
        }
    }

    override suspend fun logout(): Result<Unit> {
        // Simulate network delay
        delay(500)
        
        currentUser = null
        _authState.value = AuthState.Unauthenticated
        return Result.success(Unit)
    }

    override suspend fun getCurrentUser(): User? {
        // Simulate network delay
        delay(300)
        
        return currentUser?.also { user ->
            _authState.value = AuthState.Authenticated(user)
        }
    }

    override suspend fun refreshToken(): Result<Unit> {
        // Simulate token refresh
        delay(800)
        
        return if (currentUser != null) {
            Result.success(Unit)
        } else {
            _authState.value = AuthState.Unauthenticated
            Result.failure(Exception("リフレッシュトークンが無効です"))
        }
    }

    // Test helper methods
    fun simulateLoggedInUser() {
        val user = fakeUsers["testuser"]!!
        currentUser = user
        _authState.value = AuthState.Authenticated(user)
    }

    fun simulateLoggedOutUser() {
        currentUser = null
        _authState.value = AuthState.Unauthenticated
    }
}