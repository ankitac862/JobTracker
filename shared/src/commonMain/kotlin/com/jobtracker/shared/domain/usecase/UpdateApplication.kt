package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.repository.ApplicationRepository
import com.jobtracker.shared.util.TimeProvider

class UpdateApplication(
    private val applicationRepository: ApplicationRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(application: Application) {
        val updated = application.copy(
            updatedAtEpochMs = timeProvider.currentTimeMillis()
        )
        applicationRepository.update(updated)
    }
}
