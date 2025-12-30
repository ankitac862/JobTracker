package com.jobtracker.shared.domain.repository

import com.jobtracker.shared.domain.model.Interview
import kotlinx.coroutines.flow.Flow

interface InterviewRepository {
    fun observeByApplicationId(applicationId: String): Flow<List<Interview>>
    fun observeUpcoming(): Flow<List<Interview>>
    
    suspend fun getById(interviewId: String): Interview?
    suspend fun add(interview: Interview)
    suspend fun update(interview: Interview)
    suspend fun delete(interviewId: String)
    suspend fun syncWithRemote()
}

