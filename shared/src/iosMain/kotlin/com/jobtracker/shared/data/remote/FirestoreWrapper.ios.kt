package com.jobtracker.shared.data.remote

import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.model.Contact
import com.jobtracker.shared.domain.model.Interview
import com.jobtracker.shared.domain.model.StatusHistory
import com.jobtracker.shared.domain.model.Task
import com.jobtracker.shared.util.Result

actual class FirestoreWrapper {
    actual suspend fun upsertApplication(userId: String, application: Application): Result<Unit> {
        return Result.Error(Exception("Firestore not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun deleteApplication(userId: String, applicationId: String): Result<Unit> {
        return Result.Error(Exception("Firestore not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun getApplicationsSince(userId: String, sinceEpochMs: Long): Result<List<Application>> {
        return Result.Success(emptyList())
    }
    
    actual suspend fun upsertTask(userId: String, task: Task): Result<Unit> {
        return Result.Error(Exception("Firestore not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun deleteTask(userId: String, taskId: String): Result<Unit> {
        return Result.Error(Exception("Firestore not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun getTasksSince(userId: String, sinceEpochMs: Long): Result<List<Task>> {
        return Result.Success(emptyList())
    }
    
    actual suspend fun upsertInterview(userId: String, interview: Interview): Result<Unit> {
        return Result.Error(Exception("Firestore not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun deleteInterview(userId: String, interviewId: String): Result<Unit> {
        return Result.Error(Exception("Firestore not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun getInterviewsSince(userId: String, sinceEpochMs: Long): Result<List<Interview>> {
        return Result.Success(emptyList())
    }
    
    actual suspend fun upsertContact(userId: String, contact: Contact): Result<Unit> {
        return Result.Error(Exception("Firestore not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun deleteContact(userId: String, contactId: String): Result<Unit> {
        return Result.Error(Exception("Firestore not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun getContactsSince(userId: String, sinceEpochMs: Long): Result<List<Contact>> {
        return Result.Success(emptyList())
    }
    
    actual suspend fun upsertStatusHistory(userId: String, history: StatusHistory): Result<Unit> {
        return Result.Error(Exception("Firestore not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun getStatusHistorySince(userId: String, sinceEpochMs: Long): Result<List<StatusHistory>> {
        return Result.Success(emptyList())
    }
}
