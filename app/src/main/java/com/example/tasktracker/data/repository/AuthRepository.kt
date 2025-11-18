package com.example.tasktracker.data.repository

import com.example.tasktracker.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo

class AuthRepository {
    private val auth = SupabaseClient.getAuth()

    /**
     * Регистрация нового пользователя
     * @param email email пользователя
     * @param password пароль
     * @return успешность операции
     */
    suspend fun signUp(email: String, password: String): Result<UserInfo> {
        return try {
            val result = auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(result!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Вход существующего пользователя
     * @param email email пользователя
     * @param password пароль
     * @return успешность операции
     */
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Выход из системы
     */
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Получить текущего пользователя
     * @return UserInfo или null
     */
    suspend fun getCurrentUser(): UserInfo? {
        return try {
            auth.currentUserOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Проверка, авторизован ли пользователь
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }

    /**
     * Получить ID текущего пользователя
     */
    fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }
}
