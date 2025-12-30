package com.jobtracker.shared.data.repository

import com.jobtracker.shared.data.local.InterviewLocalDataSource
import com.jobtracker.shared.domain.model.Interview
import com.jobtracker.shared.domain.repository.InterviewRepository
import com.jobtracker.shared.util.TimeProvider
import kotlinx.coroutines.flow.Flow

class InterviewRepositoryImpl(
    private val localDataSource: InterviewLocalDataSource,
    private val timeProvider: TimeProvider
) : InterviewRepository {
    
    override fun observeByApplicationId(applicationId: String): Flow<List<Interview>> {
        return localDataSource.observeByApplicationId(applicationId)
    }
    
    override fun observeUpcoming(): Flow<List<Interview>> {
        val currentTime = timeProvider.currentTimeMillis()
        return localDataSource.observeUpcoming(currentTime)
    }
    
    override suspend fun getById(interviewId: String): Interview? {
        return localDataSource.getById(interviewId)
    }
    
    override suspend fun add(interview: Interview) {
        localDataSource.upsert(interview)
    }
    
    override suspend fun update(interview: Interview) {
        val updated = interview.copy(
            updatedAtEpochMs = timeProvider.currentTimeMillis(),
            needsSync = true
        )
        localDataSource.upsert(updated)
    }
    
    override suspend fun delete(interviewId: String) {
        val interview = getById(interviewId) ?: return
        val updated = interview.copy(
            isDeleted = true,
            updatedAtEpochMs = timeProvider.currentTimeMillis(),
            needsSync = true
        )
        localDataSource.upsert(updated)
    }
    
    override suspend fun syncWithRemote() {
    }
}

