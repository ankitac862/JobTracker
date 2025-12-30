package com.jobtracker.shared.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.jobtracker.shared.util.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual class FirebaseAuthWrapper {
    private val auth = FirebaseAuth.getInstance()
    
    actual suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid
            if (userId != null) {
                Result.Success(userId)
            } else {
                Result.Error(Exception("Sign in failed: User ID is null"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun signUpWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid
            if (userId != null) {
                Result.Success(userId)
            } else {
                Result.Error(Exception("Sign up failed: User ID is null"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    actual suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    actual fun observeAuthState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        
        // Send initial value
        trySend(auth.currentUser?.uid)
        
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }
}
