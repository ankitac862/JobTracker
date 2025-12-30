package com.jobtracker.shared.domain.model

import com.jobtracker.shared.domain.enum.ApplicationStatus
import kotlinx.serialization.Serializable

@Serializable
data class Application(
    val id: String,
    val company: String,
    val role: String,
    val location: String? = null,
    val jobUrl: String? = null,
    val source: String? = null,
    val status: ApplicationStatus,
    val appliedDateEpochMs: Long,
    val notes: String,
    val updatedAtEpochMs: Long,
    val isDeleted: Boolean = false,
    val needsSync: Boolean = true
)
