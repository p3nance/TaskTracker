package com.example.tasktracker.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasktracker.data.model.Task
import com.example.tasktracker.presentation.viewmodel.TaskViewModel

@Composable
fun TaskListContent(
    tasks: List<Task>,
    viewModel: TaskViewModel,
    emptyMessage: String
) {
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val selectedTasks by viewModel.selectedTasks.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Ошибки
            error?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Индикатор загрузки
            if (loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Список или пустое сообщение
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = emptyMessage,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            isSelected = selectedTasks.contains(task.id),
                            selectionMode = selectionMode,
                            onLongClick = {
                                if (!selectionMode) {
                                    viewModel.enterSelectionMode(task.id)
                                }
                            },
                            onClick = {
                                if (selectionMode) {
                                    viewModel.toggleTaskSelection(task.id)
                                }
                            },
                            onToggleComplete = {
                                viewModel.updateTask(
                                    task.id,
                                    isCompleted = !task.isCompleted
                                )
                            },
                            onDelete = {
                                viewModel.deleteTask(task.id)
                            }
                        )
                    }
                }
            }
        }

        // FAB для добавления задач (только на вкладке "Активные")
        if (!selectionMode && emptyMessage == "Нет активных задач") {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Добавить")
            }
        }
    }

    // Диалог добавления задачи
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, priority, dueDate ->
                viewModel.addTask(title, description, priority, dueDate)
                showAddDialog = false
            }
        )
    }
}
