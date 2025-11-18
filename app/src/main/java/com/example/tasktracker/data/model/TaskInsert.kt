package com.example.tasktracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskInsert(
    @SerialName("user_id")
    val userId: String,

    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("priority")
    val priority: TaskPriority = TaskPriority.MEDIUM,

    @SerialName("due_date")
    val dueDate: String? = null,

    @SerialName("is_completed")
    val isCompleted: Boolean = false
)
