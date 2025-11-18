package com.example.tasktracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskUpdate(
    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("is_completed")
    val isCompleted: Boolean? = null,

    @SerialName("priority")
    val priority: TaskPriority? = null,

    @SerialName("due_date")
    val dueDate: String? = null
)
