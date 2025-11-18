package com.example.tasktracker.data.repository

import com.example.tasktracker.data.api.SupabaseClient
import com.example.tasktracker.data.model.Task
import com.example.tasktracker.data.model.TaskInsert
import com.example.tasktracker.data.model.TaskPriority
import com.example.tasktracker.data.model.TaskUpdate
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskRepository {
    private val postgrest = SupabaseClient.getPostgrest()

    /**
     * Получение списка задач пользователя
     */
    fun getTasks(userId: String): Flow<List<Task>> = flow {
        try {
            val tasks = postgrest.from("tasks")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    order(column = "priority", order = Order.DESCENDING)
                    order(column = "created_at", order = Order.ASCENDING)
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
     */
    suspend fun createTask(
        userId: String,
        title: String,
        description: String = "",
        priority: TaskPriority = TaskPriority.MEDIUM,
        dueDate: String? = null
    ): Boolean {
        return try {
            val newTask = TaskInsert(
                userId = userId,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                isCompleted = false
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
     */
    suspend fun updateTask(
        taskId: String,
        title: String? = null,
        description: String? = null,
        isCompleted: Boolean? = null,
        priority: TaskPriority? = null,
        dueDate: String? = null
    ): Boolean {
        return try {
            val update = TaskUpdate(
                title = title,
                description = description,
                isCompleted = isCompleted,
                priority = priority,
                dueDate = dueDate
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

    /**
     * Удаление нескольких задач
     */
    suspend fun deleteTasks(taskIds: List<String>): Boolean {
        return try {
            taskIds.forEach { taskId ->
                postgrest.from("tasks")
                    .delete {
                        filter {
                            eq("id", taskId)
                        }
                    }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
