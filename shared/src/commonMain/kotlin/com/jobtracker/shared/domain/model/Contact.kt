package com.jobtracker.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    val id: String,
    val applicationId: String,
    val contactName: String,
    val contactRole: String? = null,
    val emailText: String? = null,
    val linkedInUrl: String? = null,
    val notesText: String? = null,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
    val isDeleted: Boolean = false,
    val needsSync: Boolean = true
)
