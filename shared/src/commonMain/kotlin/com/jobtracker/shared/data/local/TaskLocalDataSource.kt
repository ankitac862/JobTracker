package com.jobtracker.shared.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jobtracker.shared.database.JobTrackerDatabase
import com.jobtracker.shared.domain.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskLocalDataSource(
    private val database: JobTrackerDatabase
) {
    fun observeByApplicationId(applicationId: String): Flow<List<Task>> {
        return database.tasksQueries
            .selectByApplicationId(applicationId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.map { it.toDomain() } }
    }
    
    suspend fun getById(id: String): Task? {
        return database.tasksQueries
            .selectById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }
    
    suspend fun upsert(task: Task) {
        database.tasksQueries.upsert(
            id = task.id,
            applicationId = task.applicationId,
            title = task.title,
            dueDateEpochMs = task.dueDateEpochMs,
            isDone = if (task.isDone) 1L else 0L,
            updatedAtEpochMs = task.updatedAtEpochMs,
            isDeleted = if (task.isDeleted) 1L else 0L,
            needsSync = if (task.needsSync) 1L else 0L
        )
    }
    
    suspend fun softDelete(id: String, updatedAtEpochMs: Long) {
        database.tasksQueries.softDelete(updatedAtEpochMs, id)
    }

    suspend fun toggleDone(id: String, isDone: Boolean, updatedAtEpochMs: Long) {
        database.tasksQueries.toggleDone(if (isDone) 1L else 0L, updatedAtEpochMs, id)
    }

    suspend fun getPendingSync(): List<Task> {
        return database.tasksQueries
            .selectPendingSync()
            .executeAsList()
            .map { it.toDomain() }
    }

    suspend fun markSynced(id: String) {
        database.tasksQueries.markSynced(id)
    }
    
    private fun com.jobtracker.shared.database.Tasks.toDomain(): Task {
        return Task(
            id = id,
            applicationId = applicationId,
            title = title,
            dueDateEpochMs = dueDateEpochMs,
            isDone = isDone == 1L,
            updatedAtEpochMs = updatedAtEpochMs,
            isDeleted = isDeleted == 1L,
            isCompleted = false,
            completedAtEpochMs = null,
            needsSync = needsSync == 1L
        )
    }
}
