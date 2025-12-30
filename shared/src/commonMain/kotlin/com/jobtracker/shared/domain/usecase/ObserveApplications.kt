package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.repository.ApplicationRepository
import kotlinx.coroutines.flow.Flow

class ObserveApplications(
    private val applicationRepository: ApplicationRepository
) {
    operator fun invoke(): Flow<List<Application>> {
        return applicationRepository.observeAll()
    }
}

