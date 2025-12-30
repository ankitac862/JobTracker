package com.jobtracker.shared.presentation.state

data class SettingsUiState(
    val lastSyncedAtEpochMs: Long? = null,
    val isSyncing: Boolean = false,
    val syncError: String? = null,
    val currentUserId: String? = null,
    val isSignedIn: Boolean = false,
    val isAuthLoading: Boolean = false,
    val authError: String? = null
)

