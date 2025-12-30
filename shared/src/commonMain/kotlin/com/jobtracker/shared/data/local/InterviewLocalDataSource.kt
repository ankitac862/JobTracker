package com.jobtracker.shared.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jobtracker.shared.database.JobTrackerDatabase
import com.jobtracker.shared.domain.model.Interview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InterviewLocalDataSource(
    private val database: JobTrackerDatabase
) {
    fun observeByApplicationId(applicationId: String): Flow<List<Interview>> {
        return database.interviewsQueries
            .selectByApplicationId(applicationId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.map { it.toDomain() } }
    }
    
    fun observeUpcoming(currentTimeMs: Long): Flow<List<Interview>> {
        return database.interviewsQueries
            .selectUpcoming(currentTimeMs)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.map { it.toDomain() } }
    }
    
    suspend fun getById(interviewId: String): Interview? {
        return database.interviewsQueries
            .selectById(interviewId)
            .executeAsOneOrNull()
            ?.toDomain()
    }
    
    suspend fun upsert(interview: Interview) {
        database.interviewsQueries.upsert(
            interviewId = interview.interviewId,
            applicationId = interview.applicationId,
            scheduledDateEpochMs = interview.scheduledDateEpochMs,
            interviewMode = interview.interviewMode.name,
            interviewerName = interview.interviewerName,
            interviewerEmail = interview.interviewerEmail,
            location = interview.location,
            meetingLink = interview.meetingLink,
            notes = interview.notes,
            createdAtEpochMs = interview.createdAtEpochMs,
            updatedAtEpochMs = interview.updatedAtEpochMs,
            isDeleted = if (interview.isDeleted) 1L else 0L,
            needsSync = if (interview.needsSync) 1L else 0L
        )
    }
    
    suspend fun softDelete(interviewId: String, updatedAtEpochMs: Long) {
        database.interviewsQueries.softDelete(updatedAtEpochMs, interviewId)
    }

    suspend fun getPendingSync(): List<Interview> {
        return database.interviewsQueries
            .selectPendingSync()
            .executeAsList()
            .map { it.toDomain() }
    }

    suspend fun markSynced(interviewId: String) {
        database.interviewsQueries.markSynced(interviewId)
    }
    
    private fun com.jobtracker.shared.database.Interviews.toDomain(): Interview {
        return Interview(
            interviewId = interviewId,
            applicationId = applicationId,
            scheduledDateEpochMs = scheduledDateEpochMs,
            interviewMode = com.jobtracker.shared.domain.enum.InterviewMode.valueOf(interviewMode),
            interviewerName = interviewerName,
            interviewerEmail = interviewerEmail,
            location = location,
            meetingLink = meetingLink,
            notes = notes,
            createdAtEpochMs = createdAtEpochMs,
            updatedAtEpochMs = updatedAtEpochMs,
            isDeleted = isDeleted == 1L,
            needsSync = needsSync == 1L
        )
    }
}

