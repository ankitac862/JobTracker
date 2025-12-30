package com.jobtracker.shared.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.jobtracker.shared.domain.enum.ApplicationStatus
import com.jobtracker.shared.domain.enum.InterviewMode
import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.model.Contact
import com.jobtracker.shared.domain.model.Interview
import com.jobtracker.shared.domain.model.StatusHistory
import com.jobtracker.shared.domain.model.Task
import com.jobtracker.shared.util.Result
import kotlinx.coroutines.tasks.await

actual class FirestoreWrapper {
    private val firestore = FirebaseFirestore.getInstance()
    
    // ===== Applications =====
    actual suspend fun upsertApplication(userId: String, application: Application): Result<Unit> {
        return try {
            val data = hashMapOf(
                "id" to application.id,
                "company" to application.company,
                "role" to application.role,
                "location" to application.location,
                "jobUrl" to application.jobUrl,
                "source" to application.source,
                "status" to application.status.name,
                "appliedDateEpochMs" to application.appliedDateEpochMs,
                "notes" to application.notes,
                "updatedAtEpochMs" to application.updatedAtEpochMs,
                "isDeleted" to application.isDeleted
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("applications")
                .document(application.id)
                .set(data)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun deleteApplication(userId: String, applicationId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("applications")
                .document(applicationId)
                .delete()
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun getApplicationsSince(userId: String, sinceEpochMs: Long): Result<List<Application>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("applications")
                .whereGreaterThan("updatedAtEpochMs", sinceEpochMs)
                .get()
                .await()
            
            val applications = snapshot.documents.mapNotNull { doc ->
                try {
                    Application(
                        id = doc.getString("id") ?: return@mapNotNull null,
                        company = doc.getString("company") ?: return@mapNotNull null,
                        role = doc.getString("role") ?: return@mapNotNull null,
                        location = doc.getString("location"),
                        jobUrl = doc.getString("jobUrl"),
                        source = doc.getString("source"),
                        status = ApplicationStatus.valueOf(doc.getString("status") ?: "APPLIED"),
                        appliedDateEpochMs = doc.getLong("appliedDateEpochMs") ?: 0L,
                        notes = doc.getString("notes") ?: "",
                        updatedAtEpochMs = doc.getLong("updatedAtEpochMs") ?: 0L,
                        isDeleted = doc.getBoolean("isDeleted") ?: false,
                        needsSync = false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.Success(applications)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ===== Tasks =====
    actual suspend fun upsertTask(userId: String, task: Task): Result<Unit> {
        return try {
            val data = hashMapOf(
                "id" to task.id,
                "applicationId" to task.applicationId,
                "title" to task.title,
                "dueDateEpochMs" to task.dueDateEpochMs,
                "isDone" to task.isDone,
                "updatedAtEpochMs" to task.updatedAtEpochMs,
                "isDeleted" to task.isDeleted
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(task.id)
                .set(data)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun deleteTask(userId: String, taskId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun getTasksSince(userId: String, sinceEpochMs: Long): Result<List<Task>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("tasks")
                .whereGreaterThan("updatedAtEpochMs", sinceEpochMs)
                .get()
                .await()
            
            val tasks = snapshot.documents.mapNotNull { doc ->
                try {
                    Task(
                        id = doc.getString("id") ?: return@mapNotNull null,
                        applicationId = doc.getString("applicationId") ?: return@mapNotNull null,
                        title = doc.getString("title") ?: return@mapNotNull null,
                        dueDateEpochMs = doc.getLong("dueDateEpochMs"),
                        isDone = doc.getBoolean("isDone") ?: false,
                        updatedAtEpochMs = doc.getLong("updatedAtEpochMs") ?: 0L,
                        isDeleted = doc.getBoolean("isDeleted") ?: false,
                        isCompleted = false,
                        completedAtEpochMs = null,
                        needsSync = false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.Success(tasks)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ===== Interviews =====
    actual suspend fun upsertInterview(userId: String, interview: Interview): Result<Unit> {
        return try {
            val data = hashMapOf(
                "interviewId" to interview.interviewId,
                "applicationId" to interview.applicationId,
                "scheduledDateEpochMs" to interview.scheduledDateEpochMs,
                "interviewMode" to interview.interviewMode.name,
                "interviewerName" to interview.interviewerName,
                "interviewerEmail" to interview.interviewerEmail,
                "location" to interview.location,
                "meetingLink" to interview.meetingLink,
                "notes" to interview.notes,
                "createdAtEpochMs" to interview.createdAtEpochMs,
                "updatedAtEpochMs" to interview.updatedAtEpochMs,
                "isDeleted" to interview.isDeleted
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("interviews")
                .document(interview.interviewId)
                .set(data)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun deleteInterview(userId: String, interviewId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("interviews")
                .document(interviewId)
                .delete()
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun getInterviewsSince(userId: String, sinceEpochMs: Long): Result<List<Interview>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("interviews")
                .whereGreaterThan("updatedAtEpochMs", sinceEpochMs)
                .get()
                .await()
            
            val interviews = snapshot.documents.mapNotNull { doc ->
                try {
                    Interview(
                        interviewId = doc.getString("interviewId") ?: return@mapNotNull null,
                        applicationId = doc.getString("applicationId") ?: return@mapNotNull null,
                        scheduledDateEpochMs = doc.getLong("scheduledDateEpochMs") ?: 0L,
                        interviewMode = InterviewMode.valueOf(doc.getString("interviewMode") ?: "ONLINE"),
                        interviewerName = doc.getString("interviewerName"),
                        interviewerEmail = doc.getString("interviewerEmail"),
                        location = doc.getString("location"),
                        meetingLink = doc.getString("meetingLink"),
                        notes = doc.getString("notes"),
                        createdAtEpochMs = doc.getLong("createdAtEpochMs") ?: 0L,
                        updatedAtEpochMs = doc.getLong("updatedAtEpochMs") ?: 0L,
                        isDeleted = doc.getBoolean("isDeleted") ?: false,
                        needsSync = false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.Success(interviews)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ===== Contacts =====
    actual suspend fun upsertContact(userId: String, contact: Contact): Result<Unit> {
        return try {
            val data = hashMapOf(
                "id" to contact.id,
                "applicationId" to contact.applicationId,
                "contactName" to contact.contactName,
                "contactRole" to contact.contactRole,
                "emailText" to contact.emailText,
                "linkedInUrl" to contact.linkedInUrl,
                "notesText" to contact.notesText,
                "createdAtEpochMs" to contact.createdAtEpochMs,
                "updatedAtEpochMs" to contact.updatedAtEpochMs,
                "isDeleted" to contact.isDeleted
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("contacts")
                .document(contact.id)
                .set(data)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun deleteContact(userId: String, contactId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("contacts")
                .document(contactId)
                .delete()
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun getContactsSince(userId: String, sinceEpochMs: Long): Result<List<Contact>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("contacts")
                .whereGreaterThan("updatedAtEpochMs", sinceEpochMs)
                .get()
                .await()
            
            val contacts = snapshot.documents.mapNotNull { doc ->
                try {
                    Contact(
                        id = doc.getString("id") ?: return@mapNotNull null,
                        applicationId = doc.getString("applicationId") ?: return@mapNotNull null,
                        contactName = doc.getString("contactName") ?: return@mapNotNull null,
                        contactRole = doc.getString("contactRole"),
                        emailText = doc.getString("emailText"),
                        linkedInUrl = doc.getString("linkedInUrl"),
                        notesText = doc.getString("notesText"),
                        createdAtEpochMs = doc.getLong("createdAtEpochMs") ?: 0L,
                        updatedAtEpochMs = doc.getLong("updatedAtEpochMs") ?: 0L,
                        isDeleted = doc.getBoolean("isDeleted") ?: false,
                        needsSync = false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.Success(contacts)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    // ===== Status History =====
    actual suspend fun upsertStatusHistory(userId: String, history: StatusHistory): Result<Unit> {
        return try {
            val data = hashMapOf(
                "id" to history.id,
                "applicationId" to history.applicationId,
                "fromStatus" to history.fromStatus?.name,
                "toStatus" to history.toStatus.name,
                "changedAtEpochMs" to history.changedAtEpochMs,
                "note" to history.note
            )
            
            firestore.collection("users")
                .document(userId)
                .collection("statusHistory")
                .document(history.id)
                .set(data)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun getStatusHistorySince(userId: String, sinceEpochMs: Long): Result<List<StatusHistory>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("statusHistory")
                .whereGreaterThan("changedAtEpochMs", sinceEpochMs)
                .get()
                .await()
            
            val history = snapshot.documents.mapNotNull { doc ->
                try {
                    StatusHistory(
                        id = doc.getString("id") ?: return@mapNotNull null,
                        applicationId = doc.getString("applicationId") ?: return@mapNotNull null,
                        fromStatus = doc.getString("fromStatus")?.let { ApplicationStatus.valueOf(it) },
                        toStatus = ApplicationStatus.valueOf(doc.getString("toStatus") ?: "APPLIED"),
                        changedAtEpochMs = doc.getLong("changedAtEpochMs") ?: 0L,
                        note = doc.getString("note"),
                        needsSync = false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.Success(history)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
