package com.jobtracker.shared.presentation.viewmodel

import com.jobtracker.shared.domain.enum.ApplicationStatus
import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.repository.ApplicationRepository
import com.jobtracker.shared.domain.usecase.AddApplication
import com.jobtracker.shared.domain.usecase.DeleteApplication
import com.jobtracker.shared.domain.usecase.UpdateApplication
import com.jobtracker.shared.presentation.state.ApplicationsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ApplicationsViewModel(
    private val applicationRepository: ApplicationRepository,
    private val addApplication: AddApplication,
    private val updateApplication: UpdateApplication,
    private val deleteApplication: DeleteApplication,
    private val coroutineScope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(ApplicationsUiState())
    val uiState: StateFlow<ApplicationsUiState> = _uiState.asStateFlow()
    
    private val _selectedStatus = MutableStateFlow<ApplicationStatus?>(null)
    private val _searchQuery = MutableStateFlow("")
    
    init {
        observeApplications()
    }
    
    private fun observeApplications() {
        coroutineScope.launch {
            combine(
                applicationRepository.observeAll(),
                _selectedStatus,
                _searchQuery
            ) { applications, status, query ->
                var filtered = applications
                
                if (status != null) {
                    filtered = filtered.filter { it.status == status }
                }
                
                if (query.isNotBlank()) {
                    filtered = filtered.filter {
                        it.company.contains(query, ignoreCase = true) ||
                        it.role.contains(query, ignoreCase = true)
                    }
                }
                
                Triple(applications, filtered, null)
            }
            .catch { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message,
                    isLoading = false
                )
            }
            .collect { (all, filtered, _) ->
                _uiState.value = _uiState.value.copy(
                    applications = all,
                    filteredApplications = filtered,
                    selectedStatus = _selectedStatus.value,
                    searchQuery = _searchQuery.value,
                    isLoading = false,
                    error = null
                )
            }
        }
    }
    
    fun setStatusFilter(status: ApplicationStatus?) {
        _selectedStatus.value = status
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun addNewApplication(
        company: String,
        role: String,
        location: String? = null,
        jobUrl: String? = null,
        source: String? = null,
        status: ApplicationStatus,
        appliedDateEpochMs: Long,
        notes: String = ""
    ) {
        coroutineScope.launch {
            try {
                addApplication(
                    company = company,
                    role = role,
                    location = location,
                    jobUrl = jobUrl,
                    source = source,
                    status = status,
                    appliedDateEpochMs = appliedDateEpochMs,
                    notes = notes
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateExistingApplication(application: Application) {
        coroutineScope.launch {
            try {
                updateApplication(application)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun deleteApplicationById(id: String) {
        coroutineScope.launch {
            try {
                deleteApplication(id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // Helper function for iOS to get current state
    fun getCurrentState(): ApplicationsUiState {
        return uiState.value
    }
}
