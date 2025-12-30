package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.repository.ApplicationRepository
import com.jobtracker.shared.util.TimeProvider
import com.jobtracker.shared.util.UuidProvider

class AddApplication(
    private val applicationRepository: ApplicationRepository,
    private val uuidProvider: UuidProvider,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(
        company: String,
        role: String,
        location: String? = null,
        jobUrl: String? = null,
        source: String? = null,
        status: com.jobtracker.shared.domain.enum.ApplicationStatus,
        appliedDateEpochMs: Long,
        notes: String = ""
    ) {
        val now = timeProvider.currentTimeMillis()
        val application = Application(
            id = uuidProvider.generate(),
            company = company,
            role = role,
            location = location,
            jobUrl = jobUrl,
            source = source,
            status = status,
            appliedDateEpochMs = appliedDateEpochMs,
            notes = notes,
            updatedAtEpochMs = now,
            isDeleted = false
        )
        applicationRepository.add(application)
    }
}
