package com.example.quiz_app.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.util.UUID

/**
 * クイズセッションの管理を担当するドメインサービス
 * タイマー機能、ナビゲーション、回答管理を統合的に処理
 */
class QuizSessionManager {
    
    private var _currentSession = MutableStateFlow<QuizSessionState?>(null)
    val currentSession: StateFlow<QuizSessionState?> = _currentSession.asStateFlow()
    
    private var _remainingTime = MutableStateFlow<Long?>(null) // seconds
    val remainingTime: StateFlow<Long?> = _remainingTime.asStateFlow()
    
    private var _totalElapsedTime = MutableStateFlow(0L) // seconds
    val totalElapsedTime: StateFlow<Long> = _totalElapsedTime.asStateFlow()
    
    /**
     * 新しいクイズセッションを開始
     */
    fun startSession(
        quizId: String,
        quizTitle: String,
        questions: List<Question>,
        settings: QuizSessionSettings = QuizSessionSettings()
    ): Result<QuizSessionState> {
        return try {
            val session = QuizSessionState(
                sessionId = UUID.randomUUID().toString(),
                quizId = quizId,
                quizTitle = quizTitle,
                questions = if (settings.shuffleQuestions) questions.shuffled() else questions,
                sessionState = SessionState.IN_PROGRESS,
                startedAt = LocalDateTime.now(),
                currentQuestionStartTime = System.currentTimeMillis(),
                settings = settings
            )
            
            _currentSession.value = session
            
            // タイマー設定
            if (settings.timeLimit != null) {
                _remainingTime.value = settings.timeLimit
            }
            
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 回答を送信
     */
    fun submitAnswer(selectedOption: Int): Result<AnswerResult> {
        val session = _currentSession.value ?: return Result.failure(IllegalStateException("No active session"))
        val currentQuestion = session.getCurrentQuestion() 
            ?: return Result.failure(IllegalStateException("No current question"))
        
        return try {
            val timeSpent = (System.currentTimeMillis() - session.currentQuestionStartTime) / 1000
            val isCorrect = selectedOption == currentQuestion.correctAnswerIndex
            
            val userAnswer = UserAnswer(
                questionId = currentQuestion.id,
                selectedOption = selectedOption,
                correctOption = currentQuestion.correctAnswerIndex,
                isCorrect = isCorrect,
                timeSpent = timeSpent,
                answeredAt = LocalDateTime.now()
            )
            
            val updatedAnswers = session.answers + (session.currentQuestionIndex to userAnswer)
            val updatedSession = session.copy(
                answers = updatedAnswers,
                totalTimeSpent = session.totalTimeSpent + timeSpent
            )
            
            _currentSession.value = updatedSession
            _totalElapsedTime.value = updatedSession.totalTimeSpent
            
            val answerResult = AnswerResult(
                isCorrect = isCorrect,
                correctAnswer = currentQuestion.correctAnswerIndex,
                explanation = currentQuestion.explanation,
                timeSpent = timeSpent,
                currentScore = updatedAnswers.values.count { it.isCorrect },
                totalQuestions = session.totalQuestions
            )
            
            Result.success(answerResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 次の問題に進む
     */
    fun navigateToNext(): Result<NavigationResult> {
        val session = _currentSession.value ?: return Result.failure(IllegalStateException("No active session"))
        
        return try {
            if (!session.canNavigateNext) {
                // クイズ終了
                finishSession()
                return Result.success(NavigationResult.QUIZ_FINISHED)
            }
            
            val updatedSession = session.copy(
                currentQuestionIndex = session.currentQuestionIndex + 1,
                currentQuestionStartTime = System.currentTimeMillis()
            )
            
            _currentSession.value = updatedSession
            
            // タイマーリセット
            if (session.settings.timeLimit != null) {
                _remainingTime.value = session.settings.timeLimit
            }
            
            Result.success(NavigationResult.NEXT_QUESTION)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 前の問題に戻る
     */
    fun navigateToPrevious(): Result<NavigationResult> {
        val session = _currentSession.value ?: return Result.failure(IllegalStateException("No active session"))
        
        return try {
            if (!session.canNavigatePrevious) {
                return Result.failure(IllegalStateException("Cannot navigate to previous question"))
            }
            
            val updatedSession = session.copy(
                currentQuestionIndex = session.currentQuestionIndex - 1,
                currentQuestionStartTime = System.currentTimeMillis()
            )
            
            _currentSession.value = updatedSession
            
            Result.success(NavigationResult.PREVIOUS_QUESTION)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 指定した問題番号にジャンプ
     */
    fun navigateToQuestion(questionIndex: Int): Result<NavigationResult> {
        val session = _currentSession.value ?: return Result.failure(IllegalStateException("No active session"))
        
        return try {
            if (questionIndex < 0 || questionIndex >= session.totalQuestions) {
                return Result.failure(IllegalArgumentException("Invalid question index"))
            }
            
            if (!session.settings.allowReview && questionIndex < session.currentQuestionIndex) {
                return Result.failure(IllegalStateException("Review not allowed"))
            }
            
            val updatedSession = session.copy(
                currentQuestionIndex = questionIndex,
                currentQuestionStartTime = System.currentTimeMillis()
            )
            
            _currentSession.value = updatedSession
            
            Result.success(NavigationResult.JUMPED_TO_QUESTION)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * セッションを一時停止
     */
    fun pauseSession(): Result<Unit> {
        val session = _currentSession.value ?: return Result.failure(IllegalStateException("No active session"))
        
        return try {
            val updatedSession = session.copy(
                sessionState = SessionState.PAUSED,
                pausedAt = LocalDateTime.now()
            )
            
            _currentSession.value = updatedSession
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * セッションを再開
     */
    fun resumeSession(): Result<Unit> {
        val session = _currentSession.value ?: return Result.failure(IllegalStateException("No active session"))
        
        return try {
            val updatedSession = session.copy(
                sessionState = SessionState.IN_PROGRESS,
                resumedAt = LocalDateTime.now(),
                currentQuestionStartTime = System.currentTimeMillis()
            )
            
            _currentSession.value = updatedSession
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * セッションを終了
     */
    fun finishSession(): Result<QuizResult> {
        val session = _currentSession.value ?: return Result.failure(IllegalStateException("No active session"))
        
        return try {
            val finishedSession = session.copy(
                sessionState = SessionState.FINISHED,
                finishedAt = LocalDateTime.now()
            )
            
            _currentSession.value = finishedSession
            
            val result = finishedSession.calculateScore()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * タイマーを更新（1秒ごとに呼び出される）
     */
    fun updateTimer(): TimerUpdate {
        val session = _currentSession.value ?: return TimerUpdate.NO_SESSION
        
        if (session.sessionState != SessionState.IN_PROGRESS) {
            return TimerUpdate.SESSION_PAUSED
        }
        
        val currentTime = (System.currentTimeMillis() - session.currentQuestionStartTime) / 1000
        _totalElapsedTime.value = session.totalTimeSpent + currentTime
        
        // 制限時間がある場合
        session.settings.timeLimit?.let { timeLimit ->
            val remaining = timeLimit - currentTime
            _remainingTime.value = maxOf(0, remaining)
            
            if (remaining <= 0) {
                return TimerUpdate.TIME_UP
            }
        }
        
        return TimerUpdate.UPDATED
    }
    
    /**
     * セッションをリセット
     */
    fun resetSession() {
        _currentSession.value = null
        _remainingTime.value = null
        _totalElapsedTime.value = 0L
    }
}

/**
 * クイズセッションの状態管理用データクラス
 */
data class QuizSessionState(
    val sessionId: String,
    val quizId: String,
    val quizTitle: String,
    val questions: List<Question>,
    val currentQuestionIndex: Int = 0,
    val answers: Map<Int, UserAnswer> = emptyMap(),
    val sessionState: SessionState = SessionState.NOT_STARTED,
    val startedAt: LocalDateTime? = null,
    val finishedAt: LocalDateTime? = null,
    val pausedAt: LocalDateTime? = null,
    val resumedAt: LocalDateTime? = null,
    val totalTimeSpent: Long = 0L, // seconds
    val currentQuestionStartTime: Long = 0L, // timestamp
    val settings: QuizSessionSettings = QuizSessionSettings()
) {
    val totalQuestions: Int get() = questions.size
    val isFinished: Boolean get() = sessionState == SessionState.FINISHED
    val isPaused: Boolean get() = sessionState == SessionState.PAUSED
    val isActive: Boolean get() = sessionState == SessionState.IN_PROGRESS
    val canNavigateNext: Boolean get() = currentQuestionIndex < questions.size - 1
    val canNavigatePrevious: Boolean get() = currentQuestionIndex > 0
    
    /**
     * 現在の問題を取得
     */
    fun getCurrentQuestion(): Question? = questions.getOrNull(currentQuestionIndex)
    
    /**
     * 指定された問題の回答を取得
     */
    fun getAnswerForQuestion(questionIndex: Int): UserAnswer? = answers[questionIndex]
    
    /**
     * スコアを計算
     */
    fun calculateScore(): QuizResult {
        val correctAnswers = answers.values.count { it.isCorrect }
        val totalAnswered = answers.size
        val accuracy = if (totalAnswered > 0) correctAnswers.toFloat() / totalAnswered else 0f
        
        return QuizResult(
            sessionId = sessionId,
            quizId = quizId,
            quizTitle = quizTitle,
            totalQuestions = totalQuestions,
            answeredQuestions = totalAnswered,
            correctAnswers = correctAnswers,
            accuracy = accuracy,
            totalTimeSpent = totalTimeSpent,
            completedAt = finishedAt ?: LocalDateTime.now()
        )
    }
}

/**
 * ユーザーの回答情報
 */
data class UserAnswer(
    val questionId: String,
    val selectedOption: Int,
    val correctOption: Int,
    val isCorrect: Boolean,
    val timeSpent: Long, // seconds
    val answeredAt: LocalDateTime
)

/**
 * セッションの状態
 */
enum class SessionState {
    NOT_STARTED,
    IN_PROGRESS,
    PAUSED,
    FINISHED
}

/**
 * クイズセッションの設定
 */
data class QuizSessionSettings(
    val timeLimit: Long? = null, // seconds per question, null = no limit
    val autoAdvance: Boolean = false, // 自動で次の問題に進む
    val showTimer: Boolean = true,
    val allowReview: Boolean = true, // 問題の見直しを許可
    val shuffleQuestions: Boolean = false,
    val shuffleOptions: Boolean = false
)

/**
 * クイズ結果
 */
data class QuizResult(
    val sessionId: String,
    val quizId: String,
    val quizTitle: String,
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val correctAnswers: Int,
    val accuracy: Float,
    val totalTimeSpent: Long, // seconds
    val completedAt: LocalDateTime
)

/**
 * 回答結果
 */
data class AnswerResult(
    val isCorrect: Boolean,
    val correctAnswer: Int,
    val explanation: String,
    val timeSpent: Long,
    val currentScore: Int,
    val totalQuestions: Int
)

/**
 * ナビゲーション結果
 */
enum class NavigationResult {
    NEXT_QUESTION,
    PREVIOUS_QUESTION,
    JUMPED_TO_QUESTION,
    QUIZ_FINISHED
}

/**
 * タイマー更新結果
 */
enum class TimerUpdate {
    UPDATED,
    TIME_UP,
    SESSION_PAUSED,
    NO_SESSION
}