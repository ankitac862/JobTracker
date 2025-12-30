package com.jobtracker.shared.data.repository

import com.jobtracker.shared.data.local.StatusHistoryLocalDataSource
import com.jobtracker.shared.domain.model.StatusHistory
import com.jobtracker.shared.domain.repository.StatusHistoryRepository
import kotlinx.coroutines.flow.Flow

class StatusHistoryRepositoryImpl(
    private val localDataSource: StatusHistoryLocalDataSource
) : StatusHistoryRepository {
    
    override fun observeByApplicationId(applicationId: String): Flow<List<StatusHistory>> {
        return localDataSource.observeByApplicationId(applicationId)
    }
    
    override suspend fun insert(statusHistory: StatusHistory) {
        localDataSource.insert(statusHistory)
    }
}

