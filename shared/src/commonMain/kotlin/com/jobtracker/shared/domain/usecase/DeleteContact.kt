package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.repository.ContactRepository

class DeleteContact(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(id: String) {
        contactRepository.delete(id)
    }
}

