package com.jobtracker.shared.domain.model

import com.jobtracker.shared.domain.enum.ApplicationStatus
import kotlinx.serialization.Serializable

@Serializable
data class StatusHistory(
    val id: String,
    val applicationId: String,
    val fromStatus: ApplicationStatus?,
    val toStatus: ApplicationStatus,
    val changedAtEpochMs: Long,
    val note: String? = null,
    val needsSync: Boolean = true
)

