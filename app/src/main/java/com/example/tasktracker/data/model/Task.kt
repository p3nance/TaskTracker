package com.example.tasktracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    @SerialName("id")
    val id: String = "",

    @SerialName("user_id")
    val userId: String = "",

    @SerialName("title")
    val title: String = "",

    @SerialName("description")
    val description: String? = null,

    @SerialName("is_completed")
    val isCompleted: Boolean = false,

    @SerialName("priority")
    val priority: TaskPriority = TaskPriority.MEDIUM,

    @SerialName("due_date")
    val dueDate: String? = null, // ISO 8601 формат: "2025-11-20"

    @SerialName("created_at")
    val createdAt: String = "",

    @SerialName("updated_at")
    val updatedAt: String = ""
)

@Serializable
enum class TaskPriority {
    @SerialName("low")
    LOW,

    @SerialName("medium")
    MEDIUM,

    @SerialName("high")
    HIGH;

    fun getDisplayName(): String = when (this) {
        LOW -> "Легкая"
        MEDIUM -> "Средняя"
        HIGH -> "Сложная"
    }
}
