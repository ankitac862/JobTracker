package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.repository.ApplicationRepository

class DeleteApplication(
    private val applicationRepository: ApplicationRepository
) {
    suspend operator fun invoke(id: String) {
        applicationRepository.delete(id)
    }
}
