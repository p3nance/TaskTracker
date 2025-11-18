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

    @SerialName("created_at")
    val createdAt: String = "",

    @SerialName("updated_at")
    val updatedAt: String = ""
)

@Serializable
data class User(
    @SerialName("id")
    val id: String,

    @SerialName("email")
    val email: String
)