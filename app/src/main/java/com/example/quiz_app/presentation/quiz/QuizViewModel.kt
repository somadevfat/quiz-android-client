package com.example.quiz_app.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app.domain.Question
import com.example.quiz_app.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            quizRepository.getQuizDetail(quizId)
                .onSuccess { questions ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        questions = questions,
                        currentQuestionIndex = 0,
                        totalQuestions = questions.size
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun selectAnswer(optionIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedAnswerIndex = optionIndex)
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        val currentQuestion = currentState.questions.getOrNull(currentState.currentQuestionIndex)
        
        if (currentQuestion != null && currentState.selectedAnswerIndex != null) {
            val isCorrect = currentState.selectedAnswerIndex == currentQuestion.correctAnswerIndex
            
            _uiState.value = currentState.copy(
                isAnswerCorrect = isCorrect,
                showResult = true,
                score = if (isCorrect) currentState.score + 1 else currentState.score
            )
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentQuestionIndex + 1
        
        if (nextIndex < currentState.questions.size) {
            _uiState.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswerIndex = null,
                showResult = false,
                isAnswerCorrect = null
            )
        } else {
            // Quiz finished
            _uiState.value = currentState.copy(
                isQuizFinished = true
            )
        }
    }

    fun resetQuiz() {
        _uiState.value = QuizUiState()
    }

    fun getCurrentQuestion(): Question? {
        val currentState = _uiState.value
        return currentState.questions.getOrNull(currentState.currentQuestionIndex)
    }
}

data class QuizUiState(
    val isLoading: Boolean = false,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
    val showResult: Boolean = false,
    val isAnswerCorrect: Boolean? = null,
    val isQuizFinished: Boolean = false,
    val errorMessage: String? = null
)