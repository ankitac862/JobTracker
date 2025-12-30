package com.jobtracker.shared.domain.repository

import com.jobtracker.shared.domain.model.Task
import com.jobtracker.shared.domain.usecase.ToggleTaskDone
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeByApplicationId(applicationId: String): Flow<List<Task>>
    suspend fun getById(id: String): Task?
    suspend fun add(task: Task)
    suspend fun update(task: Task)
    suspend fun delete(id: String)
    suspend fun toggleDone(id: String)
    suspend fun toggleTaskDone(id: String)
}
