import Foundation
import Combine
import shared

class SettingsViewModel: ObservableObject {
    @Published var isSignedIn: Bool = false
    @Published var currentUserId: String? = nil
    @Published var isAuthLoading: Bool = false
    @Published var authError: String? = nil
    
    @Published var isSyncing: Bool = false
    @Published var lastSyncedAtEpochMs: Int64? = nil
    @Published var syncError: String? = nil
    
    private let authWrapper: FirebaseAuthWrapper
    private let syncCoordinator: SyncCoordinator
    private var syncObservationTask: ConcurrentTaskNever<Void>?
    
    init() {
        // Get instances from Koin DI
        self.authWrapper = KoinHelper.shared.getFirebaseAuthWrapper()
        self.syncCoordinator = KoinHelper.shared.getSyncCoordinator()
        
        // Observe sync state
        observeSyncState()
    }
    
    deinit {
        syncObservationTask?.cancel()
    }
    
    private func observeSyncState() {
        // Use polling approach
        syncObservationTask = SwiftConcurrency.run { [weak self] in
            guard let self = self else { return }
            
            var lastState: SyncState? = nil
            
            while !SwiftConcurrency.isCancelled {
                // Get current state using helper function
                let currentState = self.syncCoordinator.getCurrentSyncState()
                
                // Only update if state changed
                if currentState !== lastState {
                    await MainActor.run {
                        self.isSyncing = currentState.isSyncing
                        self.lastSyncedAtEpochMs = currentState.lastSyncedAtEpochMs?.int64Value
                        
                        if let error = currentState.syncError {
                            self.syncError = error.message ?? "Unknown error"
                        } else {
                            self.syncError = nil
                        }
                    }
                    lastState = currentState
                }
                
                // Poll every 100ms
                try? await SwiftConcurrency.sleep(nanoseconds: 100_000_000)
            }
        }
    }
    
    func signIn(email: String, password: String) {
        isAuthLoading = true
        authError = nil
        
        SwiftConcurrency.run {
            do {
                let result = try await self.authWrapper.signInWithEmail(email: email, password: password)
                
                await MainActor.run {
                    self.isAuthLoading = false
                    
                    if let errorResult = result as? ResultError {
                        self.authError = errorResult.exception.message ?? "Sign in failed"
                    } else if let success = result as? ResultSuccess {
                        self.currentUserId = success.data as? String
                        self.isSignedIn = true
                        self.authError = nil
                    }
                }
            } catch {
                await MainActor.run {
                    self.isAuthLoading = false
                    self.authError = error.localizedDescription
                }
            }
        }
    }
    
    func signUp(email: String, password: String) {
        isAuthLoading = true
        authError = nil
        
        SwiftConcurrency.run {
            do {
                let result = try await self.authWrapper.signUpWithEmail(email: email, password: password)
                
                await MainActor.run {
                    self.isAuthLoading = false
                    
                    if let errorResult = result as? ResultError {
                        self.authError = errorResult.exception.message ?? "Sign up failed"
                    } else if let success = result as? ResultSuccess {
                        self.currentUserId = success.data as? String
                        self.isSignedIn = true
                        self.authError = nil
                    }
                }
            } catch {
                await MainActor.run {
                    self.isAuthLoading = false
                    self.authError = error.localizedDescription
                }
            }
        }
    }
    
    func signOut() {
        SwiftConcurrency.run {
            do {
                _ = try await self.authWrapper.signOut()
                
                await MainActor.run {
                    self.currentUserId = nil
                    self.isSignedIn = false
                    self.authError = nil
                }
            } catch {
                await MainActor.run {
                    self.authError = error.localizedDescription
                }
            }
        }
    }
    
    func syncNow(userId: String) {
        SwiftConcurrency.run {
            do {
                try await self.syncCoordinator.syncNow(userId: userId)
            } catch {
                await MainActor.run {
                    self.syncError = error.localizedDescription
                }
            }
        }
    }
}
