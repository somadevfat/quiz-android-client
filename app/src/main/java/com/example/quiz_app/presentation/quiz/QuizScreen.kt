package com.example.quiz_app.presentation.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    quizId: String,
    onNavigateBack: () -> Unit,
    onQuizFinished: (score: Int, totalQuestions: Int) -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    LaunchedEffect(uiState.isQuizFinished) {
        if (uiState.isQuizFinished) {
            onQuizFinished(uiState.score, uiState.totalQuestions)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quiz ${uiState.currentQuestionIndex + 1}/${uiState.totalQuestions}",
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .testTag("loadingIndicator"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "エラーが発生しました",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "不明なエラー",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("戻る")
                        }
                    }
                }
            }

            else -> {
                QuizContent(
                    uiState = uiState,
                    onAnswerSelected = viewModel::selectAnswer,
                    onSubmitAnswer = viewModel::submitAnswer,
                    onNextQuestion = viewModel::nextQuestion,
                    onNavigateBack = onNavigateBack,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun QuizContent(
    uiState: QuizUiState,
    onAnswerSelected: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQuestion = uiState.questions.getOrNull(uiState.currentQuestionIndex)

    if (currentQuestion == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("問題が見つかりません")
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress indicator
        item {
            LinearProgressIndicator(
                progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.totalQuestions },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Question text
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = currentQuestion.questionText,
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 18.sp,
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Options
        itemsIndexed(currentQuestion.options) { index, option ->
            OptionCard(
                option = option,
                isSelected = uiState.selectedAnswerIndex == index,
                isCorrect = if (uiState.showResult) index == currentQuestion.correctAnswerIndex else null,
                isUserAnswer = if (uiState.showResult) uiState.selectedAnswerIndex == index else false,
                onClick = { if (!uiState.showResult) onAnswerSelected(index) }
            )
        }

        // Result and explanation
        if (uiState.showResult) {
            item {
                ResultCard(
                    isCorrect = uiState.isAnswerCorrect ?: false,
                    explanation = currentQuestion.explanation,
                    score = uiState.score,
                    totalQuestions = uiState.totalQuestions
                )
            }
        }

        // Action buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("終了")
                }

                if (!uiState.showResult) {
                    Button(
                        onClick = onSubmitAnswer,
                        modifier = Modifier.weight(1f),
                        enabled = uiState.selectedAnswerIndex != null
                    ) {
                        Text("回答")
                    }
                } else {
                    Button(
                        onClick = onNextQuestion,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (uiState.currentQuestionIndex == uiState.totalQuestions - 1) 
                                "結果を見る" 
                            else 
                                "次の問題"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    option: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isUserAnswer: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect == true -> Color(0xFF4CAF50) // Green for correct
        isCorrect == false && isUserAnswer -> Color(0xFFF44336) // Red for wrong user answer
        isSelected && isCorrect == null -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        isCorrect == true || (isCorrect == false && isUserAnswer) -> Color.White
        isSelected && isCorrect == null -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isSelected && isCorrect == null) 
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
        else null
    ) {
        Text(
            text = option,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}

@Composable
private fun ResultCard(
    isCorrect: Boolean,
    explanation: String,
    score: Int,
    totalQuestions: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) 
                Color(0xFFE8F5E8) 
            else 
                Color(0xFFFFEBEE)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isCorrect) "正解！" else "不正解",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "現在のスコア: $score/$totalQuestions",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "解説",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}