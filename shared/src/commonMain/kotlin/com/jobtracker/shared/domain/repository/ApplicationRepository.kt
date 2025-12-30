package com.jobtracker.shared.domain.repository

import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.enum.ApplicationStatus
import kotlinx.coroutines.flow.Flow

interface ApplicationRepository {
    fun observeAll(): Flow<List<Application>>
    fun observeById(id: String): Flow<Application?>
    fun observeByStatus(status: ApplicationStatus): Flow<List<Application>>
    fun searchByKeyword(keyword: String): Flow<List<Application>>
    
    suspend fun getById(id: String): Application?
    suspend fun add(application: Application)
    suspend fun update(application: Application)
    suspend fun delete(id: String)
}

