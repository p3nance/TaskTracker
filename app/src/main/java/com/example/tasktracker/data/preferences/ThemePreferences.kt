package com.example.tasktracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val USE_SYSTEM_THEME_KEY = booleanPreferencesKey("use_system_theme")
    }

    /**
     * Получить текущую тему
     */
    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    /**
     * Использовать системную тему
     */
    val useSystemTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[USE_SYSTEM_THEME_KEY] ?: true
        }

    /**
     * Установить темную тему
     */
    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
            preferences[USE_SYSTEM_THEME_KEY] = false
        }
    }

    /**
     * Включить системную тему
     */
    suspend fun enableSystemTheme() {
        context.dataStore.edit { preferences ->
            preferences[USE_SYSTEM_THEME_KEY] = true
        }
    }
}
