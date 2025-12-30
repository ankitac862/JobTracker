package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.model.Contact
import com.jobtracker.shared.domain.repository.ContactRepository
import com.jobtracker.shared.util.TimeProvider
import com.jobtracker.shared.util.UuidProvider

class AddContact(
    private val contactRepository: ContactRepository,
    private val uuidProvider: UuidProvider,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(
        applicationId: String,
        contactName: String,
        contactRole: String? = null,
        emailText: String? = null,
        linkedInUrl: String? = null,
        notesText: String? = null
    ) {
        val now = timeProvider.currentTimeMillis()
        val contact = Contact(
            id = uuidProvider.generate(),
            applicationId = applicationId,
            contactName = contactName,
            contactRole = contactRole,
            emailText = emailText,
            linkedInUrl = linkedInUrl,
            notesText = notesText,
            createdAtEpochMs = now,
            updatedAtEpochMs = now,
            isDeleted = false,
            needsSync = true
        )
        contactRepository.add(contact)
    }
}

