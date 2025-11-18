package com.example.tasktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktracker.data.model.AuthState
import com.example.tasktracker.data.preferences.ThemePreferences
import com.example.tasktracker.presentation.ui.AuthScreen
import com.example.tasktracker.presentation.ui.MainScreen
import com.example.tasktracker.presentation.viewmodel.AuthViewModel
import com.example.tasktracker.presentation.viewmodel.ThemeViewModel
import com.example.tasktracker.ui.theme.TaskTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themePreferences = ThemePreferences(applicationContext)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModel.Factory(themePreferences)
            )

            val useSystemTheme by themeViewModel.useSystemTheme.collectAsState()
            val darkMode by themeViewModel.darkMode.collectAsState()

            val isDarkTheme = if (useSystemTheme) {
                isSystemInDarkTheme()
            } else {
                darkMode
            }

            TaskTrackerTheme(darkTheme = isDarkTheme) {
                TaskTrackerApp(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun TaskTrackerApp(
    authViewModel: AuthViewModel = viewModel(),
    themeViewModel: ThemeViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            LoadingScreen()
        }
        is AuthState.Authenticated -> {
            MainScreen(
                onSignOut = { authViewModel.signOut() },
                themeViewModel = themeViewModel
            )
        }
        is AuthState.Unauthenticated, is AuthState.Error -> {
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = { }
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}
