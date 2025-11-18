package com.example.tasktracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.model.AuthState
import com.example.tasktracker.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    // Состояние аутентификации
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    // Состояние загрузки
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Сообщение об ошибке
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        checkAuthStatus()
    }

    /**
     * Проверка статуса авторизации при запуске
     */
    private fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = repository.getCurrentUser()
                _authState.value = if (user != null) {
                    AuthState.Authenticated
                } else {
                    AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    /**
     * Регистрация нового пользователя
     */
    fun signUp(email: String, password: String) {
        // Валидация
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email и пароль не могут быть пустыми"
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "Пароль должен быть не менее 6 символов"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _errorMessage.value = null

                val result = repository.signUp(email, password)

                result.onSuccess {
                    _authState.value = AuthState.Authenticated
                    _errorMessage.value = null
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Ошибка регистрации"
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Неизвестная ошибка"
                _authState.value = AuthState.Unauthenticated
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Вход существующего пользователя
     */
    fun signIn(email: String, password: String) {
        // Валидация
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email и пароль не могут быть пустыми"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _errorMessage.value = null

                val result = repository.signIn(email, password)

                result.onSuccess {
                    _authState.value = AuthState.Authenticated
                    _errorMessage.value = null
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Ошибка входа"
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Неизвестная ошибка"
                _authState.value = AuthState.Unauthenticated
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Выход из системы
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.signOut()
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Ошибка выхода"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Очистка сообщения об ошибке
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
