package com.jobtracker.shared.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.jobtracker.shared.database.JobTrackerDatabase
import com.jobtracker.shared.domain.enum.ApplicationStatus
import com.jobtracker.shared.domain.model.StatusHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class StatusHistoryLocalDataSource(
    private val database: JobTrackerDatabase
) {
    fun observeByApplicationId(applicationId: String): Flow<List<StatusHistory>> {
        return database.statusHistoryQueries
            .observeByApplicationId(applicationId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.map { it.toDomain() } }
    }

    suspend fun getById(id: String): StatusHistory? = withContext(Dispatchers.Default) {
        database.statusHistoryQueries
            .selectById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }
    
    suspend fun insert(statusHistory: StatusHistory) {
        database.statusHistoryQueries.insert(
            id = statusHistory.id,
            applicationId = statusHistory.applicationId,
            fromStatus = statusHistory.fromStatus?.name,
            toStatus = statusHistory.toStatus.name,
            changedAtEpochMs = statusHistory.changedAtEpochMs,
            note = statusHistory.note,
            needsSync = if (statusHistory.needsSync) 1L else 0L
        )
    }

    suspend fun getPendingSync(): List<StatusHistory> = withContext(Dispatchers.Default) {
        database.statusHistoryQueries
            .selectPendingSync()
            .executeAsList()
            .map { it.toDomain() }
    }

    suspend fun markSynced(id: String) = withContext(Dispatchers.Default) {
        database.statusHistoryQueries.markSynced(id)
    }
    
    private fun com.jobtracker.shared.database.StatusHistory.toDomain(): StatusHistory {
        return StatusHistory(
            id = id,
            applicationId = applicationId,
            fromStatus = fromStatus?.let { ApplicationStatus.valueOf(it) },
            toStatus = ApplicationStatus.valueOf(toStatus),
            changedAtEpochMs = changedAtEpochMs,
            note = note,
            needsSync = needsSync == 1L
        )
    }
}

