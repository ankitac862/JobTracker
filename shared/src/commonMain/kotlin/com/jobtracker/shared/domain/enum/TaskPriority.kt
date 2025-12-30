package com.jobtracker.shared.domain.enum

import kotlinx.serialization.Serializable

@Serializable
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

