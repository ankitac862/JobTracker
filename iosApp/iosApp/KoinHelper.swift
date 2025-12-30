import Foundation
import shared

/**
 Swift NEVER talks to Koin directly.
 It only calls Kotlin factory functions.
 */
final class KoinHelper {

    static let shared = KoinHelper()
    private init() {}

    func getApplicationsViewModel() -> shared.ApplicationsViewModel {
        return IosDIKt.provideApplicationsViewModel()
    }

    func getApplicationDetailViewModel() -> shared.ApplicationDetailViewModel {
        return IosDIKt.provideApplicationDetailViewModel()
    }

    func getSettingsViewModel() -> shared.SettingsViewModel {
        return IosDIKt.provideSettingsViewModel()
    }

    func getFirebaseAuthWrapper() -> FirebaseAuthWrapper {
        return IosDIKt.provideFirebaseAuthWrapper()
    }

    func getSyncCoordinator() -> SyncCoordinator {
        return IosDIKt.provideSyncCoordinator()
    }
}
