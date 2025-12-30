package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.model.StatusHistory
import com.jobtracker.shared.domain.repository.StatusHistoryRepository
import com.jobtracker.shared.util.UuidProvider

class AddStatusHistory(
    private val statusHistoryRepository: StatusHistoryRepository,
    private val uuidProvider: UuidProvider
) {
    suspend operator fun invoke(
        applicationId: String,
        fromStatus: com.jobtracker.shared.domain.enum.ApplicationStatus?,
        toStatus: com.jobtracker.shared.domain.enum.ApplicationStatus,
        changedAtEpochMs: Long,
        note: String? = null
    ) {
        val statusHistory = StatusHistory(
            id = uuidProvider.generate(),
            applicationId = applicationId,
            fromStatus = fromStatus,
            toStatus = toStatus,
            changedAtEpochMs = changedAtEpochMs,
            note = note
        )
        statusHistoryRepository.insert(statusHistory)
    }
}

