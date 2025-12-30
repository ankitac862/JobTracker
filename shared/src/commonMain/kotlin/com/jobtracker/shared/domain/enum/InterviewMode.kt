package com.jobtracker.shared.domain.enum

import kotlinx.serialization.Serializable

@Serializable
enum class InterviewMode {
    IN_PERSON,
    VIDEO,
    PHONE,
    OTHER
}

