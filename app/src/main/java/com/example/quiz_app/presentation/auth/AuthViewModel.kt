package com.example.quiz_app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app.domain.AuthState
import com.example.quiz_app.domain.User
import com.example.quiz_app.domain.repository.AuthRepository
import com.example.quiz_app.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Monitor auth state changes from repository
        viewModelScope.launch(ioDispatcher) {
            authRepository.authState.collectLatest { state ->
                _authState.value = state
                
                // Reset UI states on successful authentication
                if (state is AuthState.Authenticated) {
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        errorMessage = null,
                        isLoginSuccessful = true
                    )
                    _registerUiState.value = _registerUiState.value.copy(
                        isLoading = false,
                        errorMessage = null,
                        isRegisterSuccessful = true
                    )
                }
            }
        }
    }

    // Login functions
    fun updateLoginUsername(username: String) {
        _loginUiState.value = _loginUiState.value.copy(username = username)
    }

    fun updateLoginPassword(password: String) {
        _loginUiState.value = _loginUiState.value.copy(password = password)
    }

    fun login() {
        val currentState = _loginUiState.value
        
        // Basic validation
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _loginUiState.value = currentState.copy(
                errorMessage = "ユーザー名とパスワードを入力してください"
            )
            return
        }

        viewModelScope.launch(ioDispatcher) {
            _loginUiState.value = currentState.copy(
                isLoading = true,
                errorMessage = null,
                isLoginSuccessful = false
            )

            val result = authRepository.login(currentState.username, currentState.password)
            
            if (result.isFailure) {
                _loginUiState.value = _loginUiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "ログインに失敗しました"
                )
            }
            // Success case is handled by authState observer
        }
    }

    // Register functions
    fun updateRegisterUsername(username: String) {
        _registerUiState.value = _registerUiState.value.copy(username = username)
    }

    fun updateRegisterEmail(email: String) {
        _registerUiState.value = _registerUiState.value.copy(email = email)
    }

    fun updateRegisterPassword(password: String) {
        _registerUiState.value = _registerUiState.value.copy(password = password)
    }

    fun updateRegisterConfirmPassword(confirmPassword: String) {
        _registerUiState.value = _registerUiState.value.copy(confirmPassword = confirmPassword)
    }

    fun register() {
        val currentState = _registerUiState.value
        
        // Basic validation
        when {
            currentState.username.isBlank() -> {
                _registerUiState.value = currentState.copy(errorMessage = "ユーザー名を入力してください")
                return
            }
            currentState.email.isBlank() -> {
                _registerUiState.value = currentState.copy(errorMessage = "メールアドレスを入力してください")
                return
            }
            currentState.password.isBlank() -> {
                _registerUiState.value = currentState.copy(errorMessage = "パスワードを入力してください")
                return
            }
            currentState.password != currentState.confirmPassword -> {
                _registerUiState.value = currentState.copy(errorMessage = "パスワードが一致しません")
                return
            }
            currentState.password.length < 6 -> {
                _registerUiState.value = currentState.copy(errorMessage = "パスワードは6文字以上で入力してください")
                return
            }
        }

        viewModelScope.launch(ioDispatcher) {
            _registerUiState.value = currentState.copy(
                isLoading = true,
                errorMessage = null,
                isRegisterSuccessful = false
            )

            val result = authRepository.register(
                currentState.username,
                currentState.email,
                currentState.password
            )
            
            if (result.isFailure) {
                _registerUiState.value = _registerUiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "ユーザー登録に失敗しました"
                )
            }
            // Success case is handled by authState observer
        }
    }

    fun logout() {
        viewModelScope.launch(ioDispatcher) {
            authRepository.logout()
            // Reset UI states
            clearLoginForm()
            clearRegisterForm()
        }
    }

    fun clearLoginError() {
        _loginUiState.value = _loginUiState.value.copy(errorMessage = null)
    }

    fun clearRegisterError() {
        _registerUiState.value = _registerUiState.value.copy(errorMessage = null)
    }

    fun clearLoginForm() {
        _loginUiState.value = LoginUiState()
    }

    fun clearRegisterForm() {
        _registerUiState.value = RegisterUiState()
    }

    fun getCurrentUser(): User? {
        return when (val state = _authState.value) {
            is AuthState.Authenticated -> state.user
            else -> null
        }
    }
}