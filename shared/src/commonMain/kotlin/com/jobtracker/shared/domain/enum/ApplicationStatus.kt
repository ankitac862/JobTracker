package com.jobtracker.shared.domain.enum

import kotlinx.serialization.Serializable

@Serializable
enum class ApplicationStatus {
    DRAFT,
    APPLIED,
    SCREENING,
    INTERVIEW,
    OFFER,
    REJECTED,
    ACCEPTED,
    WITHDRAWN
}

