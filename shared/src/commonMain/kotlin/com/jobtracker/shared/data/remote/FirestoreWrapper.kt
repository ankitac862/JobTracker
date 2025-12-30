package com.jobtracker.shared.data.remote

import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.model.Contact
import com.jobtracker.shared.domain.model.Interview
import com.jobtracker.shared.domain.model.StatusHistory
import com.jobtracker.shared.domain.model.Task
import com.jobtracker.shared.util.Result

expect class FirestoreWrapper {
    suspend fun upsertApplication(userId: String, application: Application): Result<Unit>
    suspend fun deleteApplication(userId: String, applicationId: String): Result<Unit>
    suspend fun getApplicationsSince(userId: String, sinceEpochMs: Long): Result<List<Application>>
    
    suspend fun upsertTask(userId: String, task: Task): Result<Unit>
    suspend fun deleteTask(userId: String, taskId: String): Result<Unit>
    suspend fun getTasksSince(userId: String, sinceEpochMs: Long): Result<List<Task>>
    
    suspend fun upsertInterview(userId: String, interview: Interview): Result<Unit>
    suspend fun deleteInterview(userId: String, interviewId: String): Result<Unit>
    suspend fun getInterviewsSince(userId: String, sinceEpochMs: Long): Result<List<Interview>>
    
    suspend fun upsertContact(userId: String, contact: Contact): Result<Unit>
    suspend fun deleteContact(userId: String, contactId: String): Result<Unit>
    suspend fun getContactsSince(userId: String, sinceEpochMs: Long): Result<List<Contact>>
    
    suspend fun upsertStatusHistory(userId: String, history: StatusHistory): Result<Unit>
    suspend fun getStatusHistorySince(userId: String, sinceEpochMs: Long): Result<List<StatusHistory>>
}

