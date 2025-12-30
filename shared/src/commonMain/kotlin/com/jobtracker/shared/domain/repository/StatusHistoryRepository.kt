package com.jobtracker.shared.domain.repository

import com.jobtracker.shared.domain.model.StatusHistory
import kotlinx.coroutines.flow.Flow

interface StatusHistoryRepository {
    fun observeByApplicationId(applicationId: String): Flow<List<StatusHistory>>
    suspend fun insert(statusHistory: StatusHistory)
}

