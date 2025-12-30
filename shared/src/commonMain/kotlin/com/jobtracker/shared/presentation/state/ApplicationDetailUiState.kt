package com.jobtracker.shared.presentation.state

import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.model.Contact
import com.jobtracker.shared.domain.model.Interview
import com.jobtracker.shared.domain.model.StatusHistory
import com.jobtracker.shared.domain.model.Task

data class ApplicationDetailUiState(
    val application: Application? = null,
    val statusHistory: List<StatusHistory> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val interviews: List<Interview> = emptyList(),
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
