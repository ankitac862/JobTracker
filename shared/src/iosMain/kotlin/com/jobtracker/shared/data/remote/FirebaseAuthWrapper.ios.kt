package com.jobtracker.shared.data.remote

import com.jobtracker.shared.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class FirebaseAuthWrapper {
    actual suspend fun signInWithEmail(email: String, password: String): Result<String> {
        // iOS Firebase implementation - requires CocoaPods setup
        return Result.Error(Exception("Firebase Auth not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun signUpWithEmail(email: String, password: String): Result<String> {
        return Result.Error(Exception("Firebase Auth not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun signOut(): Result<Unit> {
        return Result.Error(Exception("Firebase Auth not configured for iOS. Please follow the iOS Firebase setup guide."))
    }
    
    actual suspend fun getCurrentUserId(): String? {
        return null
    }
    
    actual fun observeAuthState(): Flow<String?> {
        return flowOf(null)
    }
}
