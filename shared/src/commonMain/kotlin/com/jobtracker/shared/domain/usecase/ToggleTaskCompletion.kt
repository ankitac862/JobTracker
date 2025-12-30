package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.repository.TaskRepository
import com.jobtracker.shared.util.TimeProvider

class ToggleTaskCompletion(
    private val taskRepository: TaskRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(taskId: String) {
        val task = taskRepository.getById(taskId) ?: return
        val now = timeProvider.currentTimeMillis()
        val updated = task.copy(
            isCompleted = !task.isCompleted,
            completedAtEpochMs = if (!task.isCompleted) now else null,
            updatedAtEpochMs = now
        )
        taskRepository.update(updated)
    }
}

