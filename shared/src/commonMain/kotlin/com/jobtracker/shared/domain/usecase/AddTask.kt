package com.jobtracker.shared.domain.usecase

import com.jobtracker.shared.domain.model.Task
import com.jobtracker.shared.domain.repository.TaskRepository
import com.jobtracker.shared.util.TimeProvider
import com.jobtracker.shared.util.UuidProvider

class AddTask(
    private val taskRepository: TaskRepository,
    private val uuidProvider: UuidProvider,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(
        applicationId: String,
        title: String,
        dueDateEpochMs: Long? = null
    ) {
        val now = timeProvider.currentTimeMillis()
        val task = Task(
            id = uuidProvider.generate(),
            applicationId = applicationId,
            title = title,
            dueDateEpochMs = dueDateEpochMs,
            isDone = false,
            updatedAtEpochMs = now,
            isDeleted = false
        )
        taskRepository.add(task)
    }
}
