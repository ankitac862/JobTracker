package com.jobtracker.shared.presentation.viewmodel

import com.jobtracker.shared.domain.model.Application
import com.jobtracker.shared.domain.repository.ApplicationRepository
import com.jobtracker.shared.domain.repository.ContactRepository
import com.jobtracker.shared.domain.repository.InterviewRepository
import com.jobtracker.shared.domain.repository.StatusHistoryRepository
import com.jobtracker.shared.domain.repository.TaskRepository
import com.jobtracker.shared.domain.usecase.AddContact
import com.jobtracker.shared.domain.usecase.AddStatusHistory
import com.jobtracker.shared.domain.usecase.AddTask
import com.jobtracker.shared.domain.usecase.DeleteContact
import com.jobtracker.shared.domain.usecase.ToggleTaskDone
import com.jobtracker.shared.domain.usecase.UpdateApplication
import com.jobtracker.shared.presentation.state.ApplicationDetailUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ApplicationDetailViewModel(
    private val applicationRepository: ApplicationRepository,
    private val statusHistoryRepository: StatusHistoryRepository,
    private val taskRepository: TaskRepository,
    private val interviewRepository: InterviewRepository,
    private val contactRepository: ContactRepository,
    private val updateApplication: UpdateApplication,
    private val addStatusHistory: AddStatusHistory,
    private val addTask: AddTask,
    private val toggleTaskDone: ToggleTaskDone,
    private val addContact: AddContact,
    private val deleteContact: DeleteContact,
    private val coroutineScope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(ApplicationDetailUiState())
    val uiState: StateFlow<ApplicationDetailUiState> = _uiState.asStateFlow()
    
    fun loadApplication(id: String) {
        coroutineScope.launch {
            combine(
                applicationRepository.observeById(id),
                statusHistoryRepository.observeByApplicationId(id),
                taskRepository.observeByApplicationId(id),
                interviewRepository.observeByApplicationId(id),
                contactRepository.observeByApplicationId(id)
            ) { application, statusHistory, tasks, interviews, contacts ->
                ApplicationDetailUiState(
                    application = application,
                    statusHistory = statusHistory,
                    tasks = tasks,
                    interviews = interviews,
                    contacts = contacts,
                    isLoading = application == null
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun updateApplication(application: Application) {
        coroutineScope.launch {
            try {
                val oldStatus = _uiState.value.application?.status
                updateApplication.invoke(application)
                
                if (oldStatus != application.status) {
                    addStatusHistory(
                        applicationId = application.id,
                        fromStatus = oldStatus,
                        toStatus = application.status,
                        changedAtEpochMs = application.updatedAtEpochMs
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun addTask(applicationId: String, title: String, dueDateEpochMs: Long? = null) {
        coroutineScope.launch {
            try {
                addTask.invoke(applicationId, title, dueDateEpochMs)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun toggleTask(taskId: String) {
        coroutineScope.launch {
            try {
                toggleTaskDone(taskId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun addContact(
        applicationId: String,
        contactName: String,
        contactRole: String?,
        emailText: String?,
        linkedInUrl: String?,
        notesText: String?
    ) {
        coroutineScope.launch {
            try {
                addContact(applicationId, contactName, contactRole, emailText, linkedInUrl, notesText)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteContact(contactId: String) {
        coroutineScope.launch {
            try {
                deleteContact(contactId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    // Helper function for iOS to get current state
    fun getCurrentState(): ApplicationDetailUiState {
        return uiState.value
    }
}

