package com.jobtracker.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val applicationId: String,
    val title: String,
    val dueDateEpochMs: Long? = null,
    val isDone: Boolean = false,
    val updatedAtEpochMs: Long,
    val isDeleted: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAtEpochMs: Long? = null,
    val needsSync: Boolean = true
)

