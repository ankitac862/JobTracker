package com.jobtracker.shared.data.remote

import com.jobtracker.shared.util.Result

expect class FirebaseAuthWrapper {
    suspend fun signInWithEmail(email: String, password: String): Result<String>
    suspend fun signUpWithEmail(email: String, password: String): Result<String>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUserId(): String?
    fun observeAuthState(): kotlinx.coroutines.flow.Flow<String?>
}

