package com.jobtracker.shared.data.sync

data class SyncState(
    val needsSync: Boolean = false,
    val lastSyncedAtEpochMs: Long? = null,
    val isSyncing: Boolean = false,
    val syncError: Throwable? = null
)

