package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.model.Interview
import com.jobtracker.shared.domain.repository.InterviewRepository
import com.jobtracker.shared.util.TimeProvider
import com.jobtracker.shared.util.UuidProvider

class AddInterview(
    private val interviewRepository: InterviewRepository,
    private val uuidProvider: UuidProvider,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(
        applicationId: String,
        scheduledDateEpochMs: Long,
        interviewMode: com.jobtracker.shared.domain.enum.InterviewMode,
        interviewerName: String? = null,
        interviewerEmail: String? = null,
        location: String? = null,
        meetingLink: String? = null,
        notes: String? = null
    ) {
        val now = timeProvider.currentTimeMillis()
        val interview = Interview(
            interviewId = uuidProvider.generate(),
            applicationId = applicationId,
            scheduledDateEpochMs = scheduledDateEpochMs,
            interviewMode = interviewMode,
            interviewerName = interviewerName,
            interviewerEmail = interviewerEmail,
            location = location,
            meetingLink = meetingLink,
            notes = notes,
            createdAtEpochMs = now,
            updatedAtEpochMs = now,
            isDeleted = false
        )
        interviewRepository.add(interview)
    }
}

