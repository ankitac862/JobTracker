import SwiftUI
import shared
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        // Initialize Firebase
        FirebaseApp.configure()
        
        // Initialize Koin dependency injection with iOS-specific module
        AppModule_iosKt.doInitKoinIos()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

