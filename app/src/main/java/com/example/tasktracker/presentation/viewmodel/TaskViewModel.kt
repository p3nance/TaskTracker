package com.example.tasktracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.model.Task
import com.example.tasktracker.data.model.TaskPriority
import com.example.tasktracker.data.repository.AuthRepository
import com.example.tasktracker.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()
    private val authRepository = AuthRepository()

    // Состояние списка задач
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    // Режим выбора
    private val _selectionMode = MutableStateFlow(false)
    val selectionMode: StateFlow<Boolean> = _selectionMode

    // Выбранные задачи
    private val _selectedTasks = MutableStateFlow<Set<String>>(emptySet())
    val selectedTasks: StateFlow<Set<String>> = _selectedTasks

    // Состояние загрузки
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val currentUserId: String
        get() = authRepository.getCurrentUserId() ?: ""

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            try {
                repository.getTasks(currentUserId).collect { tasks ->
                    _tasks.value = tasks
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addTask(
        title: String,
        description: String = "",
        priority: TaskPriority = TaskPriority.MEDIUM,
        dueDate: String? = null
    ) {
        if (title.trim().isEmpty()) {
            _error.value = "Название не может быть пустым"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.createTask(
                    currentUserId,
                    title,
                    description,
                    priority,
                    dueDate
                )

                if (success) {
                    loadTasks()
                    _error.value = null
                } else {
                    _error.value = "Ошибка при добавлении задачи"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateTask(
        taskId: String,
        title: String? = null,
        description: String? = null,
        isCompleted: Boolean? = null,
        priority: TaskPriority? = null,
        dueDate: String? = null
    ) {
        viewModelScope.launch {
            try {
                val success = repository.updateTask(
                    taskId, title, description, isCompleted, priority, dueDate
                )
                if (success) {
                    loadTasks()
                } else {
                    _error.value = "Ошибка при обновлении задачи"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                val success = repository.deleteTask(taskId)
                if (success) {
                    loadTasks()
                } else {
                    _error.value = "Ошибка при удалении задачи"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteSelectedTasks() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.deleteTasks(_selectedTasks.value.toList())
                if (success) {
                    clearSelection()
                    loadTasks()
                } else {
                    _error.value = "Ошибка при удалении задач"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun toggleTaskSelection(taskId: String) {
        val currentSelection = _selectedTasks.value.toMutableSet()
        if (currentSelection.contains(taskId)) {
            currentSelection.remove(taskId)
        } else {
            currentSelection.add(taskId)
        }
        _selectedTasks.value = currentSelection

        // Выход из режима выбора если ничего не выбрано
        if (currentSelection.isEmpty()) {
            _selectionMode.value = false
        }
    }

    fun enterSelectionMode(taskId: String) {
        _selectionMode.value = true
        _selectedTasks.value = setOf(taskId)
    }

    fun clearSelection() {
        _selectionMode.value = false
        _selectedTasks.value = emptySet()
    }

    fun clearError() {
        _error.value = null
    }
}
