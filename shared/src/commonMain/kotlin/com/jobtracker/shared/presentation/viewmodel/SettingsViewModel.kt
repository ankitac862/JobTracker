package com.jobtracker.shared.presentation.viewmodel

import com.jobtracker.shared.data.remote.FirebaseAuthWrapper
import com.jobtracker.shared.data.sync.SyncCoordinator
import com.jobtracker.shared.presentation.state.SettingsUiState
import com.jobtracker.shared.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val syncCoordinator: SyncCoordinator,
    private val firebaseAuthWrapper: FirebaseAuthWrapper,
    private val coroutineScope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        observeSyncState()
        observeAuthState()
    }
    
    private fun observeSyncState() {
        coroutineScope.launch {
            syncCoordinator.syncState.collect { syncState ->
                _uiState.value = _uiState.value.copy(
                    lastSyncedAtEpochMs = syncState.lastSyncedAtEpochMs,
                    isSyncing = syncState.isSyncing,
                    syncError = syncState.syncError?.toString()
                )
            }
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            firebaseAuthWrapper.observeAuthState().collect { userId ->
                _uiState.value = _uiState.value.copy(
                    currentUserId = userId,
                    isSignedIn = userId != null
                )
            }
        }
    }
    
    fun syncNow() {
        coroutineScope.launch {
            val userId = _uiState.value.currentUserId
            if (userId != null) {
                syncCoordinator.syncNow(userId)
            } else {
                _uiState.value = _uiState.value.copy(
                    syncError = "Cannot sync: User not signed in"
                )
            }
        }
    }

    fun signIn(email: String, password: String) {
        coroutineScope.launch {
            _uiState.value = _uiState.value.copy(isAuthLoading = true, authError = null)
            when (val result = firebaseAuthWrapper.signInWithEmail(email, password)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAuthLoading = false,
                        currentUserId = result.data,
                        isSignedIn = true
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isAuthLoading = false,
                        authError = result.exception.message
                    )
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        coroutineScope.launch {
            _uiState.value = _uiState.value.copy(isAuthLoading = true, authError = null)
            when (val result = firebaseAuthWrapper.signUpWithEmail(email, password)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAuthLoading = false,
                        currentUserId = result.data,
                        isSignedIn = true
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isAuthLoading = false,
                        authError = result.exception.message
                    )
                }
            }
        }
    }

    fun signOut() {
        coroutineScope.launch {
            when (firebaseAuthWrapper.signOut()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        currentUserId = null,
                        isSignedIn = false
                    )
                }
                is Result.Error -> {
                    // Silent fail for sign out
                }
            }
        }
    }
}

