package com.jobtracker.shared.data.repository

import com.jobtracker.shared.data.local.ApplicationLocalDataSource
import com.jobtracker.shared.data.local.StatusHistoryLocalDataSource
import com.jobtracker.shared.domain.enum.ApplicationStatus
import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.model.StatusHistory
import com.jobtracker.shared.domain.repository.ApplicationRepository
import com.jobtracker.shared.util.TimeProvider
import com.jobtracker.shared.util.UuidProvider
import kotlinx.coroutines.flow.Flow

class ApplicationRepositoryImpl(
    private val localDataSource: ApplicationLocalDataSource,
    private val statusHistoryDataSource: StatusHistoryLocalDataSource,
    private val timeProvider: TimeProvider,
    private val uuidProvider: UuidProvider
) : ApplicationRepository {
    
    override fun observeAll(): Flow<List<Application>> {
        return localDataSource.observeAll()
    }
    
    override fun observeById(id: String): Flow<Application?> {
        return localDataSource.observeById(id)
    }
    
    override fun observeByStatus(status: ApplicationStatus): Flow<List<Application>> {
        return localDataSource.observeByStatus(status)
    }
    
    override fun searchByKeyword(keyword: String): Flow<List<Application>> {
        return localDataSource.searchByKeyword(keyword)
    }
    
    override suspend fun getById(id: String): Application? {
        return localDataSource.getById(id)
    }
    
    override suspend fun add(application: Application) {
        localDataSource.upsert(application)
        // Record initial status
        val history = StatusHistory(
            id = uuidProvider.generate(),
            applicationId = application.id,
            fromStatus = null,
            toStatus = application.status,
            changedAtEpochMs = application.appliedDateEpochMs,
            note = "Application created"
        )
        statusHistoryDataSource.insert(history)
    }
    
    override suspend fun update(application: Application) {
        val oldApplication = getById(application.id)
        val updated = application.copy(
            updatedAtEpochMs = timeProvider.currentTimeMillis(),
            needsSync = true
        )
        localDataSource.upsert(updated)
        
        // Record status change if status changed
        if (oldApplication != null && oldApplication.status != application.status) {
            val history = StatusHistory(
                id = uuidProvider.generate(),
                applicationId = application.id,
                fromStatus = oldApplication.status,
                toStatus = application.status,
                changedAtEpochMs = updated.updatedAtEpochMs,
                note = null
            )
            statusHistoryDataSource.insert(history)
        }
    }
    
    override suspend fun delete(id: String) {
        val application = getById(id) ?: return
        val updated = application.copy(
            isDeleted = true,
            updatedAtEpochMs = timeProvider.currentTimeMillis(),
            needsSync = true
        )
        localDataSource.upsert(updated)
    }
}
