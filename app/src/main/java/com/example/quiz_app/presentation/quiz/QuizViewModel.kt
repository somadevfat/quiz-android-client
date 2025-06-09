package com.example.quiz_app.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app.domain.AnswerResult
import com.example.quiz_app.domain.NavigationResult
import com.example.quiz_app.domain.Question
import com.example.quiz_app.domain.QuizSessionManager
import com.example.quiz_app.domain.QuizSessionSettings
import com.example.quiz_app.domain.SessionState
import com.example.quiz_app.domain.TimerUpdate
import com.example.quiz_app.domain.repository.QuizRepository
import com.example.quiz_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val sessionManager = QuizSessionManager()
    
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    
    // セッション管理用のState
    val currentSession = sessionManager.currentSession
    val remainingTime = sessionManager.remainingTime
    val totalElapsedTime = sessionManager.totalElapsedTime
    
    init {
        startTimerJob()
        observeSessionChanges()
    }

    fun loadQuiz(quizId: String, sessionSettings: QuizSessionSettings = QuizSessionSettings()) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            quizRepository.getQuizDetail(quizId)
                .onSuccess { questions ->
                    // QuizSessionManagerでセッション開始
                    sessionManager.startSession(
                        quizId = quizId,
                        quizTitle = "Quiz", // TODO: Quiz titleを取得
                        questions = questions,
                        settings = sessionSettings
                    ).onSuccess { session ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            questions = questions,
                            currentQuestionIndex = session.currentQuestionIndex,
                            totalQuestions = questions.size,
                            sessionSettings = sessionSettings
                        )
                        
                        // Check bookmark status for initial question
                        val firstQuestion = questions.getOrNull(session.currentQuestionIndex)
                        firstQuestion?.let { checkBookmarkStatus(it.id) }
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
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
        
        if (currentState.selectedAnswerIndex != null) {
            sessionManager.submitAnswer(currentState.selectedAnswerIndex)
                .onSuccess { result ->
                    _uiState.value = currentState.copy(
                        isAnswerCorrect = result.isCorrect,
                        showResult = true,
                        score = result.currentScore,
                        answerResult = result
                    )
                }
                .onFailure { error ->
                    _uiState.value = currentState.copy(
                        errorMessage = error.message
                    )
                }
        }
    }

    fun nextQuestion() {
        sessionManager.navigateToNext()
            .onSuccess { result ->
                when (result) {
                    NavigationResult.NEXT_QUESTION -> {
                        val currentSession = sessionManager.currentSession.value
                        val newIndex = currentSession?.currentQuestionIndex ?: 0
                        _uiState.value = _uiState.value.copy(
                            currentQuestionIndex = newIndex,
                            selectedAnswerIndex = null,
                            showResult = false,
                            isAnswerCorrect = null,
                            answerResult = null,
                            isTimeUp = false
                        )
                        
                        // Check bookmark status for new question
                        val newQuestion = _uiState.value.questions.getOrNull(newIndex)
                        newQuestion?.let { checkBookmarkStatus(it.id) }
                    }
                    NavigationResult.QUIZ_FINISHED -> {
                        _uiState.value = _uiState.value.copy(
                            isQuizFinished = true
                        )
                    }
                    else -> { /* Handle other cases */ }
                }
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message
                )
            }
    }
    
    fun previousQuestion() {
        sessionManager.navigateToPrevious()
            .onSuccess {
                val currentSession = sessionManager.currentSession.value
                val newIndex = currentSession?.currentQuestionIndex ?: 0
                _uiState.value = _uiState.value.copy(
                    currentQuestionIndex = newIndex,
                    selectedAnswerIndex = null,
                    showResult = false,
                    isAnswerCorrect = null,
                    answerResult = null,
                    isTimeUp = false
                )
                
                // Check bookmark status for new question
                val newQuestion = _uiState.value.questions.getOrNull(newIndex)
                newQuestion?.let { checkBookmarkStatus(it.id) }
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message
                )
            }
    }
    
    fun jumpToQuestion(questionIndex: Int) {
        sessionManager.navigateToQuestion(questionIndex)
            .onSuccess {
                val currentSession = sessionManager.currentSession.value
                val existingAnswer = currentSession?.getAnswerForQuestion(questionIndex)
                _uiState.value = _uiState.value.copy(
                    currentQuestionIndex = questionIndex,
                    selectedAnswerIndex = existingAnswer?.selectedOption,
                    showResult = existingAnswer != null,
                    isAnswerCorrect = existingAnswer?.isCorrect,
                    answerResult = null,
                    isTimeUp = false
                )
                
                // Check bookmark status for jumped question
                val jumpedQuestion = _uiState.value.questions.getOrNull(questionIndex)
                jumpedQuestion?.let { checkBookmarkStatus(it.id) }
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message
                )
            }
    }
    
    fun pauseSession() {
        sessionManager.pauseSession()
    }
    
    fun resumeSession() {
        sessionManager.resumeSession()
    }
    
    fun resetQuiz() {
        sessionManager.resetSession()
        _uiState.value = QuizUiState()
    }
    
    fun toggleBookmark() {
        val currentQuestion = getCurrentQuestion() ?: return
        val currentState = _uiState.value
        
        viewModelScope.launch {
            if (currentState.isCurrentQuestionBookmarked) {
                userRepository.removeBookmark(currentQuestion.id)
                    .onSuccess {
                        _uiState.value = currentState.copy(
                            isCurrentQuestionBookmarked = false
                        )
                    }
            } else {
                userRepository.addBookmark(
                    questionId = currentQuestion.id,
                    quizId = "quiz", // TODO: Get actual quiz ID
                    notes = null
                ).onSuccess {
                    _uiState.value = currentState.copy(
                        isCurrentQuestionBookmarked = true
                    )
                }
            }
        }
    }
    
    private fun checkBookmarkStatus(questionId: String) {
        viewModelScope.launch {
            userRepository.isBookmarked(questionId).collect { isBookmarked ->
                _uiState.value = _uiState.value.copy(
                    isCurrentQuestionBookmarked = isBookmarked
                )
            }
        }
    }

    fun getCurrentQuestion(): Question? {
        val currentState = _uiState.value
        return currentState.questions.getOrNull(currentState.currentQuestionIndex)
    }
    
    private fun startTimerJob() {
        viewModelScope.launch {
            while (isActive) {
                val timerUpdate = sessionManager.updateTimer()
                
                when (timerUpdate) {
                    TimerUpdate.TIME_UP -> {
                        // 時間切れ - 自動で次の問題に進むかタイムアウト処理
                        if (_uiState.value.sessionSettings.autoAdvance) {
                            nextQuestion()
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isTimeUp = true
                            )
                        }
                    }
                    TimerUpdate.UPDATED -> {
                        // 通常のタイマー更新 - UIStateは自動更新される
                    }
                    TimerUpdate.SESSION_PAUSED,
                    TimerUpdate.NO_SESSION -> {
                        // セッションが無いか一時停止中 - 何もしない
                    }
                }
                
                delay(1000) // 1秒間隔
            }
        }
    }
    
    private fun observeSessionChanges() {
        viewModelScope.launch {
            combine(
                sessionManager.currentSession,
                sessionManager.remainingTime,
                sessionManager.totalElapsedTime
            ) { session, remaining, elapsed ->
                Triple(session, remaining, elapsed)
            }.collect { (session, remainingTime, elapsedTime) ->
                session?.let {
                    _uiState.value = _uiState.value.copy(
                        sessionState = it.sessionState,
                        remainingTime = remainingTime,
                        totalElapsedTime = elapsedTime,
                        canNavigateNext = it.canNavigateNext,
                        canNavigatePrevious = it.canNavigatePrevious
                    )
                }
            }
        }
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
    val errorMessage: String? = null,
    // 新しいセッション管理関連のフィールド
    val sessionState: SessionState = SessionState.NOT_STARTED,
    val sessionSettings: QuizSessionSettings = QuizSessionSettings(),
    val remainingTime: Long? = null, // seconds
    val totalElapsedTime: Long = 0, // seconds
    val isTimeUp: Boolean = false,
    val canNavigateNext: Boolean = false,
    val canNavigatePrevious: Boolean = false,
    val answerResult: AnswerResult? = null,
    val isCurrentQuestionBookmarked: Boolean = false
)