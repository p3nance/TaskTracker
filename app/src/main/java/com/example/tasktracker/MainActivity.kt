package com.example.tasktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktracker.data.model.AuthState
import com.example.tasktracker.presentation.ui.AuthScreen
import com.example.tasktracker.presentation.ui.TaskScreen
import com.example.tasktracker.presentation.viewmodel.AuthViewModel
import com.example.tasktracker.ui.theme.TaskTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskTrackerTheme {
                TaskTrackerApp()
            }
        }
    }
}

/**
 * Главный экран приложения с навигацией
 */
@Composable
fun TaskTrackerApp(authViewModel: AuthViewModel = viewModel()) {
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            // Показываем индикатор загрузки при проверке статуса авторизации
            LoadingScreen()
        }
        is AuthState.Authenticated -> {
            // Пользователь авторизован - показываем экран задач
            TaskScreen(
                onSignOut = {
                    authViewModel.signOut()
                }
            )
        }
        is AuthState.Unauthenticated, is AuthState.Error -> {
            // Пользователь не авторизован - показываем экран входа
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    // Навигация происходит автоматически через authState
                }
            )
        }
    }
}

/**
 * Экран загрузки
 */
@Composable
fun LoadingScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}
