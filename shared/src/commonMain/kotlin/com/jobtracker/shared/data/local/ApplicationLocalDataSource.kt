package com.jobtracker.shared.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.jobtracker.shared.database.JobTrackerDatabase
import com.jobtracker.shared.domain.enum.ApplicationStatus
import com.jobtracker.shared.domain.model.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ApplicationLocalDataSource(
    private val database: JobTrackerDatabase
) {
    fun observeAll(): Flow<List<Application>> {
        return database.applicationsQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.map { it.toDomain() } }
    }
    
    fun observeById(id: String): Flow<Application?> {
        return database.applicationsQueries
            .selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }
    }
    
    fun observeByStatus(status: ApplicationStatus): Flow<List<Application>> {
        return database.applicationsQueries
            .selectByStatus(status.name)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.map { it.toDomain() } }
    }
    
    fun searchByKeyword(keyword: String): Flow<List<Application>> {
        return database.applicationsQueries
            .searchByKeyword(keyword, keyword)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.map { it.toDomain() } }
    }
    
    suspend fun getById(id: String): Application? {
        return database.applicationsQueries
            .selectById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }
    
    suspend fun upsert(application: Application) {
        database.applicationsQueries.upsert(
            id = application.id,
            company = application.company,
            role = application.role,
            location = application.location,
            jobUrl = application.jobUrl,
            source = application.source,
            status = application.status.name,
            appliedDateEpochMs = application.appliedDateEpochMs,
            notes = application.notes,
            updatedAtEpochMs = application.updatedAtEpochMs,
            isDeleted = if (application.isDeleted) 1L else 0L,
            needsSync = if (application.needsSync) 1L else 0L
        )
    }
    
    suspend fun softDelete(id: String, updatedAtEpochMs: Long) {
        database.applicationsQueries.softDelete(updatedAtEpochMs, id)
    }

    suspend fun getPendingSync(): List<Application> {
        return database.applicationsQueries
            .selectPendingSync()
            .executeAsList()
            .map { it.toDomain() }
    }

    suspend fun markSynced(id: String) {
        database.applicationsQueries.markSynced(id)
    }
    
    private fun com.jobtracker.shared.database.Applications.toDomain(): Application {
        return Application(
            id = id,
            company = company,
            role = role,
            location = location,
            jobUrl = jobUrl,
            source = source,
            status = ApplicationStatus.valueOf(status),
            appliedDateEpochMs = appliedDateEpochMs,
            notes = notes,
            updatedAtEpochMs = updatedAtEpochMs,
            isDeleted = isDeleted == 1L,
            needsSync = needsSync == 1L
        )
    }
}
