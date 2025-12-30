package com.jobtracker.shared.data.sync

import com.jobtracker.shared.data.local.ApplicationLocalDataSource
import com.jobtracker.shared.data.local.ContactLocalDataSource
import com.jobtracker.shared.data.local.InterviewLocalDataSource
import com.jobtracker.shared.data.local.StatusHistoryLocalDataSource
import com.jobtracker.shared.data.local.TaskLocalDataSource
import com.jobtracker.shared.data.remote.FirebaseAuthWrapper
import com.jobtracker.shared.data.remote.FirestoreWrapper
import com.jobtracker.shared.util.Result
import com.jobtracker.shared.util.TimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SyncCoordinator(
    private val applicationLocalDataSource: ApplicationLocalDataSource,
    private val taskLocalDataSource: TaskLocalDataSource,
    private val interviewLocalDataSource: InterviewLocalDataSource,
    private val contactLocalDataSource: ContactLocalDataSource,
    private val statusHistoryLocalDataSource: StatusHistoryLocalDataSource,
    private val firestoreWrapper: FirestoreWrapper,
    private val firebaseAuthWrapper: FirebaseAuthWrapper,
    private val timeProvider: TimeProvider,
    private val coroutineScope: CoroutineScope
) {
    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    init {
        observeAuthState()
    }
    
    private fun observeAuthState() {
        coroutineScope.launch {
            firebaseAuthWrapper.observeAuthState().collect { userId ->
                if (userId != null) {
                    performInitialSync(userId)
                }
            }
        }
    }
    
    private suspend fun performInitialSync(userId: String) {
        _syncState.value = _syncState.value.copy(isSyncing = true)
        
        val lastSyncMs = _syncState.value.lastSyncedAtEpochMs ?: 0L
        val engine = SyncEngine(
            applicationLocalDataSource = applicationLocalDataSource,
            taskLocalDataSource = taskLocalDataSource,
            interviewLocalDataSource = interviewLocalDataSource,
            contactLocalDataSource = contactLocalDataSource,
            statusHistoryLocalDataSource = statusHistoryLocalDataSource,
            firestoreWrapper = firestoreWrapper,
            timeProvider = timeProvider,
            userId = userId
        )
        
        when (val result = engine.performFullSync(lastSyncMs)) {
            is Result.Success -> {
                _syncState.value = SyncState(
                    needsSync = false,
                    lastSyncedAtEpochMs = timeProvider.currentTimeMillis(),
                    isSyncing = false,
                    syncError = null
                )
            }
            is Result.Error -> {
                _syncState.value = _syncState.value.copy(
                    isSyncing = false,
                    syncError = result.exception
                )
            }
        }
    }
    
    suspend fun syncNow(userId: String) {
        _syncState.value = _syncState.value.copy(isSyncing = true, syncError = null)
        
        val lastSyncMs = _syncState.value.lastSyncedAtEpochMs ?: 0L
        val engine = SyncEngine(
            applicationLocalDataSource = applicationLocalDataSource,
            taskLocalDataSource = taskLocalDataSource,
            interviewLocalDataSource = interviewLocalDataSource,
            contactLocalDataSource = contactLocalDataSource,
            statusHistoryLocalDataSource = statusHistoryLocalDataSource,
            firestoreWrapper = firestoreWrapper,
            timeProvider = timeProvider,
            userId = userId
        )
        
        when (val result = engine.performFullSync(lastSyncMs)) {
            is Result.Success -> {
                _syncState.value = SyncState(
                    needsSync = false,
                    lastSyncedAtEpochMs = timeProvider.currentTimeMillis(),
                    isSyncing = false,
                    syncError = null
                )
            }
            is Result.Error -> {
                _syncState.value = _syncState.value.copy(
                    isSyncing = false,
                    syncError = result.exception
                )
            }
        }
    }
    
    // Helper function for iOS to get current state
    fun getCurrentSyncState(): SyncState {
        return syncState.value
    }
}

