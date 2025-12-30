package com.jobtracker.shared.data.repository

import com.jobtracker.shared.data.local.TaskLocalDataSource
import com.jobtracker.shared.domain.model.Task
import com.jobtracker.shared.domain.repository.TaskRepository
import com.jobtracker.shared.util.TimeProvider
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val localDataSource: TaskLocalDataSource,
    private val timeProvider: TimeProvider
) : TaskRepository {
    
    override fun observeByApplicationId(applicationId: String): Flow<List<Task>> {
        return localDataSource.observeByApplicationId(applicationId)
    }
    
    override suspend fun getById(id: String): Task? {
        return localDataSource.getById(id)
    }
    
    override suspend fun add(task: Task) {
        localDataSource.upsert(task)
    }
    
    override suspend fun update(task: Task) {
        val updated = task.copy(updatedAtEpochMs = timeProvider.currentTimeMillis())
        localDataSource.upsert(updated)
    }
    
    override suspend fun delete(id: String) {
        val task = getById(id) ?: return
        val updated = task.copy(
            isDeleted = true,
            updatedAtEpochMs = timeProvider.currentTimeMillis()
        )
        localDataSource.upsert(updated)
    }
    
    override suspend fun toggleDone(id: String) {
        val task = getById(id) ?: return
        val updated = task.copy(
            isDone = !task.isDone,
            updatedAtEpochMs = timeProvider.currentTimeMillis()
        )
        localDataSource.upsert(updated)
    }

    override suspend fun toggleTaskDone(id: String) {
        val task = getById(id) ?: return
        val updated = task.copy(
            isDone = !task.isDone,
            updatedAtEpochMs = timeProvider.currentTimeMillis()
        )
        localDataSource.upsert(updated)
    }
}
