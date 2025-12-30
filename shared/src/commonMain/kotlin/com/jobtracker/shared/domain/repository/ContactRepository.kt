package com.jobtracker.shared.domain.repository

import com.jobtracker.shared.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun observeByApplicationId(applicationId: String): Flow<List<Contact>>
    fun observeById(id: String): Flow<Contact?>
    suspend fun getById(id: String): Contact?
    suspend fun add(contact: Contact)
    suspend fun update(contact: Contact)
    suspend fun delete(id: String)
}

