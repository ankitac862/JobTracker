package com.jobtracker.shared.data.sync

import com.jobtracker.shared.data.local.ApplicationLocalDataSource
import com.jobtracker.shared.data.local.ContactLocalDataSource
import com.jobtracker.shared.data.local.InterviewLocalDataSource
import com.jobtracker.shared.data.local.StatusHistoryLocalDataSource
import com.jobtracker.shared.data.local.TaskLocalDataSource
import com.jobtracker.shared.data.remote.FirestoreWrapper
import com.jobtracker.shared.util.Result
import com.jobtracker.shared.util.TimeProvider

class SyncEngine(
    private val applicationLocalDataSource: ApplicationLocalDataSource,
    private val taskLocalDataSource: TaskLocalDataSource,
    private val interviewLocalDataSource: InterviewLocalDataSource,
    private val contactLocalDataSource: ContactLocalDataSource,
    private val statusHistoryLocalDataSource: StatusHistoryLocalDataSource,
    private val firestoreWrapper: FirestoreWrapper,
    private val timeProvider: TimeProvider,
    private val userId: String
) {
    suspend fun pushLocalChanges(): Result<Unit> {
        return try {
            // Push applications
            val pendingApps = applicationLocalDataSource.getPendingSync()
            pendingApps.forEach { app ->
                if (app.isDeleted) {
                    firestoreWrapper.deleteApplication(userId, app.id)
                } else {
                    firestoreWrapper.upsertApplication(userId, app)
                }
                applicationLocalDataSource.markSynced(app.id)
            }
            
            // Push tasks
            val pendingTasks = taskLocalDataSource.getPendingSync()
            pendingTasks.forEach { task ->
                if (task.isDeleted) {
                    firestoreWrapper.deleteTask(userId, task.id)
                } else {
                    firestoreWrapper.upsertTask(userId, task)
                }
                taskLocalDataSource.markSynced(task.id)
            }
            
            // Push interviews
            val pendingInterviews = interviewLocalDataSource.getPendingSync()
            pendingInterviews.forEach { interview ->
                if (interview.isDeleted) {
                    firestoreWrapper.deleteInterview(userId, interview.interviewId)
                } else {
                    firestoreWrapper.upsertInterview(userId, interview)
                }
                interviewLocalDataSource.markSynced(interview.interviewId)
            }
            
            // Push contacts
            val pendingContacts = contactLocalDataSource.getPendingSync()
            pendingContacts.forEach { contact ->
                if (contact.isDeleted) {
                    firestoreWrapper.deleteContact(userId, contact.id)
                } else {
                    firestoreWrapper.upsertContact(userId, contact)
                }
                contactLocalDataSource.markSynced(contact.id)
            }
            
            // Push status history (no deletion, append-only)
            val pendingHistory = statusHistoryLocalDataSource.getPendingSync()
            pendingHistory.forEach { history ->
                firestoreWrapper.upsertStatusHistory(userId, history)
                statusHistoryLocalDataSource.markSynced(history.id)
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    suspend fun pullRemoteUpdates(lastSyncEpochMs: Long): Result<Unit> {
        return try {
            // Pull applications since last sync
            when (val appsResult = firestoreWrapper.getApplicationsSince(userId, lastSyncEpochMs)) {
                is Result.Success -> {
                    appsResult.data.forEach { remoteApp ->
                        val localApp = applicationLocalDataSource.getById(remoteApp.id)
                        if (localApp == null || remoteApp.updatedAtEpochMs > localApp.updatedAtEpochMs) {
                            applicationLocalDataSource.upsert(remoteApp.copy(needsSync = false))
                        }
                    }
                }
                is Result.Error -> return Result.Error(appsResult.exception)
            }
            
            // Pull tasks
            when (val tasksResult = firestoreWrapper.getTasksSince(userId, lastSyncEpochMs)) {
                is Result.Success -> {
                    tasksResult.data.forEach { remoteTask ->
                        val localTask = taskLocalDataSource.getById(remoteTask.id)
                        if (localTask == null || remoteTask.updatedAtEpochMs > localTask.updatedAtEpochMs) {
                            taskLocalDataSource.upsert(remoteTask.copy(needsSync = false))
                        }
                    }
                }
                is Result.Error -> return Result.Error(tasksResult.exception)
            }
            
            // Pull interviews
            when (val interviewsResult = firestoreWrapper.getInterviewsSince(userId, lastSyncEpochMs)) {
                is Result.Success -> {
                    interviewsResult.data.forEach { remoteInterview ->
                        val localInterview = interviewLocalDataSource.getById(remoteInterview.interviewId)
                        if (localInterview == null || remoteInterview.updatedAtEpochMs > localInterview.updatedAtEpochMs) {
                            interviewLocalDataSource.upsert(remoteInterview.copy(needsSync = false))
                        }
                    }
                }
                is Result.Error -> return Result.Error(interviewsResult.exception)
            }
            
            // Pull contacts
            when (val contactsResult = firestoreWrapper.getContactsSince(userId, lastSyncEpochMs)) {
                is Result.Success -> {
                    contactsResult.data.forEach { remoteContact ->
                        val localContact = contactLocalDataSource.getById(remoteContact.id)
                        if (localContact == null || remoteContact.updatedAtEpochMs > localContact.updatedAtEpochMs) {
                            contactLocalDataSource.upsert(remoteContact.copy(needsSync = false))
                        }
                    }
                }
                is Result.Error -> return Result.Error(contactsResult.exception)
            }
            
            // Pull status history
            when (val historyResult = firestoreWrapper.getStatusHistorySince(userId, lastSyncEpochMs)) {
                is Result.Success -> {
                    historyResult.data.forEach { remoteHistory ->
                        val localHistory = statusHistoryLocalDataSource.getById(remoteHistory.id)
                        if (localHistory == null) {
                            statusHistoryLocalDataSource.insert(remoteHistory)
                            statusHistoryLocalDataSource.markSynced(remoteHistory.id)
                        }
                    }
                }
                is Result.Error -> return Result.Error(historyResult.exception)
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    suspend fun performFullSync(lastSyncEpochMs: Long): Result<Unit> {
        // Push first, then pull
        return when (val pushResult = pushLocalChanges()) {
            is Result.Success -> pullRemoteUpdates(lastSyncEpochMs)
            is Result.Error -> pushResult
        }
    }
}
