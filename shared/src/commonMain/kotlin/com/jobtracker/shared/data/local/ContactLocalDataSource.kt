package com.jobtracker.shared.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.jobtracker.shared.database.JobTrackerDatabase
import com.jobtracker.shared.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ContactLocalDataSource(
    private val database: JobTrackerDatabase
) {
    fun observeByApplicationId(applicationId: String): Flow<List<Contact>> {
        return database.contactsQueries
            .selectByApplicationId(applicationId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.map { it.toDomain() } }
    }

    fun observeById(id: String): Flow<Contact?> {
        return database.contactsQueries
            .selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }
    }

    suspend fun getById(id: String): Contact? = withContext(Dispatchers.Default) {
        database.contactsQueries
            .selectById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    suspend fun upsert(contact: Contact) = withContext(Dispatchers.Default) {
        database.contactsQueries.upsert(
            id = contact.id,
            applicationId = contact.applicationId,
            contactName = contact.contactName,
            contactRole = contact.contactRole,
            emailText = contact.emailText,
            linkedInUrl = contact.linkedInUrl,
            notesText = contact.notesText,
            createdAtEpochMs = contact.createdAtEpochMs,
            updatedAtEpochMs = contact.updatedAtEpochMs,
            isDeleted = if (contact.isDeleted) 1L else 0L,
            needsSync = if (contact.needsSync) 1L else 0L
        )
    }

    suspend fun softDelete(id: String, timestamp: Long) = withContext(Dispatchers.Default) {
        database.contactsQueries.softDelete(timestamp, id)
    }

    suspend fun getPendingSync(): List<Contact> = withContext(Dispatchers.Default) {
        database.contactsQueries
            .selectPendingSync()
            .executeAsList()
            .map { it.toDomain() }
    }

    suspend fun markSynced(id: String) = withContext(Dispatchers.Default) {
        database.contactsQueries.markSynced(id)
    }

    private fun com.jobtracker.shared.database.Contacts.toDomain(): Contact {
        return Contact(
            id = id,
            applicationId = applicationId,
            contactName = contactName,
            contactRole = contactRole,
            emailText = emailText,
            linkedInUrl = linkedInUrl,
            notesText = notesText,
            createdAtEpochMs = createdAtEpochMs,
            updatedAtEpochMs = updatedAtEpochMs,
            isDeleted = isDeleted == 1L,
            needsSync = needsSync == 1L
        )
    }
}

