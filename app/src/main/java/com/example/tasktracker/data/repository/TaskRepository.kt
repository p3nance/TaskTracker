package com.example.tasktracker.data.repository

import com.example.tasktracker.data.api.SupabaseClient
import com.example.tasktracker.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskRepository {
    private val postgrest = SupabaseClient.getPostgrest()

    /**
     * Получение списка задач пользователя
     * @param userId идентификатор пользователя
     * @return Flow со списком задач
     */
    fun getTasks(userId: String): Flow<List<Task>> = flow {
        try {
            val tasks = postgrest.from("tasks")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Task>()
            emit(tasks)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    /**
     * Создание новой задачи
     * @param userId идентификатор пользователя
     * @param title название задачи
     * @param description описание задачи
     * @return успешность операции
     */
    suspend fun createTask(
        userId: String,
        title: String,
        description: String = ""
    ): Boolean {
        return try {
            // Создаем объект Task с данными (без id, created_at, updated_at - их сгенерирует Supabase)
            @kotlinx.serialization.Serializable
            data class TaskInsert(
                val user_id: String,
                val title: String,
                val description: String,
                val is_completed: Boolean = false
            )

            val newTask = TaskInsert(
                user_id = userId,
                title = title,
                description = description,
                is_completed = false
            )

            postgrest.from("tasks").insert(newTask)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Обновление задачи
     * @param taskId идентификатор задачи
     * @param title новое название
     * @param isCompleted новый статус выполнения
     * @return успешность операции
     */
    suspend fun updateTask(
        taskId: String,
        title: String,
        isCompleted: Boolean
    ): Boolean {
        return try {
            @kotlinx.serialization.Serializable
            data class TaskUpdate(
                val title: String,
                val is_completed: Boolean
            )

            val update = TaskUpdate(
                title = title,
                is_completed = isCompleted
            )

            postgrest.from("tasks")
                .update(update) {
                    filter {
                        eq("id", taskId)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Удаление задачи
     * @param taskId идентификатор задачи
     * @return успешность операции
     */
    suspend fun deleteTask(taskId: String): Boolean {
        return try {
            postgrest.from("tasks")
                .delete {
                    filter {
                        eq("id", taskId)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
