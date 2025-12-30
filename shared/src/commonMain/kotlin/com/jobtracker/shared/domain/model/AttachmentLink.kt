package com.jobtracker.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentLink(
    val attachmentLinkId: String,
    val applicationId: String,
    val fileName: String,
    val fileUrl: String,
    val fileType: String? = null,
    val fileSizeBytes: Long? = null,
    val uploadedAtEpochMs: Long,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
    val isDeleted: Boolean = false
)

