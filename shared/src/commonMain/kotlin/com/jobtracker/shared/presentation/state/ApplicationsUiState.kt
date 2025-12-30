package com.jobtracker.shared.presentation.state

import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.enum.ApplicationStatus

data class ApplicationsUiState(
    val applications: List<Application> = emptyList(),
    val filteredApplications: List<Application> = emptyList(),
    val selectedStatus: ApplicationStatus? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
