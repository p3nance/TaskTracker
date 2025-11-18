package com.example.tasktracker.presentation.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktracker.data.model.Task
import com.example.tasktracker.data.model.TaskPriority
import com.example.tasktracker.presentation.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel = viewModel(),
    onSignOut: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val selectedTasks by viewModel.selectedTasks.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (selectionMode) {
                TopAppBar(
                    title = { Text("Выбрано: ${selectedTasks.size}") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Filled.Close, "Отменить")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.deleteSelectedTasks() }) {
                            Icon(Icons.Filled.Delete, "Удалить выбранные", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Мои задачи") },
                    actions = {
                        IconButton(onClick = onSignOut) {
                            Icon(Icons.Filled.ExitToApp, "Выйти")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!selectionMode) {
                FloatingActionButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(Icons.Filled.Add, "Добавить")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Сообщения об ошибках
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
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Список задач
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Нет задач",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Нажмите + чтобы добавить",
                            style = MaterialTheme.typography.bodySmall,
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
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItemCard(
    task: Task,
    isSelected: Boolean,
    selectionMode: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> Color(0xFFE57373)
        TaskPriority.MEDIUM -> Color(0xFFFFB74D)
        TaskPriority.LOW -> Color(0xFF81C784)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            ),
        colors = if (isSelected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Индикатор приоритета
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Заголовок
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )

                // Описание
                if (!task.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Метки (приоритет и срок)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chip приоритета
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                task.priority.getDisplayName(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = priorityColor.copy(alpha = 0.2f),
                            labelColor = priorityColor
                        ),
                        modifier = Modifier.height(24.dp)
                    )

                    // Chip срока выполнения
                    task.dueDate?.let { date ->
                        AssistChip(
                            onClick = { },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    formatDate(date),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }

            // Кнопки действий
            if (!selectionMode) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Кнопка выполнено/не выполнено
                    if (task.isCompleted) {
                        FilledTonalIconButton(
                            onClick = onToggleComplete,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFF81C784).copy(alpha = 0.2f)
                            )
                        ) {
                            Icon(
                                Icons.Filled.Check,
                                "Выполнено",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    } else {
                        OutlinedIconButton(
                            onClick = onToggleComplete
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                "Отметить выполненным"
                            )
                        }
                    }

                    // Кнопка удаления
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                // Чекбокс в режиме выбора
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, TaskPriority, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var dueDate by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая задача") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                // Выбор приоритета
                Text("Сложность:", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.getDisplayName()) },
                            leadingIcon = if (priority == p) {
                                { Icon(Icons.Filled.Done, null, Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                // Выбор даты
                OutlinedButton(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                calendar.set(year, month, day)
                                dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(calendar.time)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.DateRange, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(dueDate?.let { "Срок: ${formatDate(it)}" } ?: "Выбрать срок")
                }

                if (dueDate != null) {
                    TextButton(
                        onClick = { dueDate = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Очистить дату")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description, priority, dueDate)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("ru"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}