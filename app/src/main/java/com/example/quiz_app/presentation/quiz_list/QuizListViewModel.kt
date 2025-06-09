package com.example.quiz_app.presentation.quiz_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app.domain.Quiz
import com.example.quiz_app.domain.repository.QuizRepository
import com.example.quiz_app.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizListUiState(
    val quizzes: List<Quiz> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class QuizListViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuizListUiState())
    val uiState: StateFlow<QuizListUiState> = _uiState.asStateFlow()
    
    init {
        loadQuizzes()
    }
    
    private fun loadQuizzes() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            quizRepository.getQuizzes()
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "An unknown error occurred"
                    )
                }
                .collect { quizzes ->
                    _uiState.value = _uiState.value.copy(
                        quizzes = quizzes,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }
    
    fun retry() {
        loadQuizzes()
    }
}