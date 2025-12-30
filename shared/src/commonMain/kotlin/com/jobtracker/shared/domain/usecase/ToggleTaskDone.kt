package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.repository.TaskRepository
import com.jobtracker.shared.util.TimeProvider

class ToggleTaskDone(
    private val taskRepository: TaskRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(taskId: String, isDone: Boolean) {
        val task = taskRepository.getById(taskId) ?: return
        val updated = task.copy(
            isDone = isDone,
            updatedAtEpochMs = timeProvider.currentTimeMillis(),
            needsSync = true
        )
        taskRepository.update(updated)
    }
    suspend operator fun invoke(taskId: String) {
        taskRepository.toggleTaskDone(taskId)
    }
}

