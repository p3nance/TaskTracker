package com.example.tasktracker.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktracker.presentation.viewmodel.TaskViewModel
import com.example.tasktracker.presentation.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSignOut: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Активные", "Выполненные")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои задачи") },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Filled.Settings, "Настройки")
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Filled.ExitToApp, "Выйти")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Вкладки
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                        icon = {
                            Icon(
                                if (index == 0) Icons.Filled.CheckCircleOutline
                                else Icons.Filled.CheckCircle,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Содержимое вкладок
            when (selectedTab) {
                0 -> ActiveTasksScreen()
                1 -> CompletedTasksScreen()
            }
        }
    }

    // Диалог настроек темы
    if (showSettingsDialog) {
        SettingsDialog(
            themeViewModel = themeViewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }
}

@Composable
fun ActiveTasksScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    val activeTasks = tasks.filter { !it.isCompleted }

    TaskListContent(
        tasks = activeTasks,
        viewModel = viewModel,
        emptyMessage = "Нет активных задач"
    )
}

@Composable
fun CompletedTasksScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    val completedTasks = tasks.filter { it.isCompleted }

    TaskListContent(
        tasks = completedTasks,
        viewModel = viewModel,
        emptyMessage = "Нет выполненных задач"
    )
}

@Composable
fun SettingsDialog(
    themeViewModel: ThemeViewModel,
    onDismiss: () -> Unit
) {
    val useSystemTheme by themeViewModel.useSystemTheme.collectAsState()
    val darkMode by themeViewModel.darkMode.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Settings, null) },
        title = { Text("Настройки темы") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Системная тема
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Следовать системной теме")
                    Switch(
                        checked = useSystemTheme,
                        onCheckedChange = {
                            if (it) {
                                themeViewModel.enableSystemTheme()
                            }
                        }
                    )
                }

                if (!useSystemTheme) {
                    Divider()

                    // Светлая тема
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Светлая тема")
                        RadioButton(
                            selected = !darkMode,
                            onClick = { themeViewModel.setDarkMode(false) }
                        )
                    }

                    // Темная тема
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Темная тема")
                        RadioButton(
                            selected = darkMode,
                            onClick = { themeViewModel.setDarkMode(true) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}
