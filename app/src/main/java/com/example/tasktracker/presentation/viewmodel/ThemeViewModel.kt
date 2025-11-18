package com.example.tasktracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tasktracker.data.preferences.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val darkMode: StateFlow<Boolean> = themePreferences.darkMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val useSystemTheme: StateFlow<Boolean> = themePreferences.useSystemTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themePreferences.setDarkMode(isDark)
        }
    }

    fun enableSystemTheme() {
        viewModelScope.launch {
            themePreferences.enableSystemTheme()
        }
    }

    class Factory(private val themePreferences: ThemePreferences) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                return ThemeViewModel(themePreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
