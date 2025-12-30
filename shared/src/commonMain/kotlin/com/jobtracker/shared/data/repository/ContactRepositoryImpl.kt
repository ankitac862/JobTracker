package com.jobtracker.shared.data.repository

import com.jobtracker.shared.data.local.ContactLocalDataSource
import com.jobtracker.shared.domain.model.Contact
import com.jobtracker.shared.domain.repository.ContactRepository
import com.jobtracker.shared.util.TimeProvider
import kotlinx.coroutines.flow.Flow

class ContactRepositoryImpl(
    private val localDataSource: ContactLocalDataSource,
    private val timeProvider: TimeProvider
) : ContactRepository {

    override fun observeByApplicationId(applicationId: String): Flow<List<Contact>> {
        return localDataSource.observeByApplicationId(applicationId)
    }

    override fun observeById(id: String): Flow<Contact?> {
        return localDataSource.observeById(id)
    }

    override suspend fun getById(id: String): Contact? {
        return localDataSource.getById(id)
    }

    override suspend fun add(contact: Contact) {
        localDataSource.upsert(contact)
    }

    override suspend fun update(contact: Contact) {
        val updated = contact.copy(
            updatedAtEpochMs = timeProvider.currentTimeMillis(),
            needsSync = true
        )
        localDataSource.upsert(updated)
    }

    override suspend fun delete(id: String) {
        localDataSource.softDelete(id, timeProvider.currentTimeMillis())
    }
}

