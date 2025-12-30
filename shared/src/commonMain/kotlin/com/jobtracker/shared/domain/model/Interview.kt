package com.jobtracker.shared.domain.model

import com.jobtracker.shared.domain.enum.InterviewMode
import kotlinx.serialization.Serializable

@Serializable
data class Interview(
    val interviewId: String,
    val applicationId: String,
    val scheduledDateEpochMs: Long,
    val interviewMode: InterviewMode,
    val interviewerName: String? = null,
    val interviewerEmail: String? = null,
    val location: String? = null,
    val meetingLink: String? = null,
    val notes: String? = null,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
    val isDeleted: Boolean = false,
    val needsSync: Boolean = true
)

