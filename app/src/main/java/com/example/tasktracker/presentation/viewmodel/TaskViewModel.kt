package com.example.tasktracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.model.Task
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

    // Состояние загрузки
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ИЗМЕНЕНО: Получаем userId из AuthRepository вместо тестового значения
    private val currentUserId: String
        get() = authRepository.getCurrentUserId() ?: ""

    init {
        loadTasks()
    }

    /**
     * Загрузка списка задач
     */
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

    /**
     * Добавление новой задачи
     * @param title название задачи
     * @param description описание задачи
     */
    fun addTask(title: String, description: String = "") {
        // Валидация
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
                    description
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

    /**
     * Обновление задачи
     * @param taskId идентификатор задачи
     * @param title название
     * @param isCompleted статус выполнения
     */
    fun updateTask(taskId: String, title: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                val success = repository.updateTask(taskId, title, isCompleted)
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

    /**
     * Удаление задачи
     * @param taskId идентификатор задачи
     */
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

    /**
     * Очистка сообщения об ошибке
     */
    fun clearError() {
        _error.value = null
    }
}
