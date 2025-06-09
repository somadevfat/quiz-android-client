package com.example.quiz_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quiz_app.domain.AuthState
import com.example.quiz_app.presentation.auth.AuthViewModel
import com.example.quiz_app.presentation.auth.LoginScreen
import com.example.quiz_app.presentation.quiz.QuizScreen
import com.example.quiz_app.presentation.quiz_list.QuizListScreen
import com.example.quiz_app.ui.theme.QuizAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuizAppNavigation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun QuizAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("quiz_list") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.Unauthenticated, is AuthState.Error -> {
                navController.navigate("login") {
                    popUpTo("quiz_list") { inclusive = true }
                }
            }
            AuthState.Loading -> { /* No navigation action needed */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = when (authState) {
            is AuthState.Authenticated -> "quiz_list"
            else -> "login"
        },
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToQuizList = {
                    navController.navigate("quiz_list") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("quiz_list") {
            QuizListScreen(
                onStartQuiz = { quizId ->
                    navController.navigate("quiz/$quizId")
                },
                onLogout = {
                    authViewModel.logout()
                }
            )
        }

        composable("quiz/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuizScreen(
                quizId = quizId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onQuizFinished = { score, totalQuestions ->
                    // For now, just navigate back to quiz list
                    // Later, we can navigate to a results screen
                    navController.popBackStack()
                }
            )
        }
    }
}