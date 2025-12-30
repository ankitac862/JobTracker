# Job Tracker App

A cross-platform mobile application built with Kotlin Multiplatform (KMP) to help you track your job applications, interviews, and tasks all in one place. Available for both Android and iOS.

## What is This Project?

This is a job application tracker that lets you:
- **Track Applications**: Keep a record of all your job applications with company name, role, status, and other details
- **Manage Tasks**: Create and track tasks related to each application (follow-ups, prep work, etc.)
- **Schedule Interviews**: Record interview details including date, mode, and interviewer information
- **Sync Across Devices**: Your data syncs to Firebase Firestore so you can access it from any device
- **Filter & Search**: Quickly find applications by status or search by company/role name

## Tech Stack

### Shared Code (Kotlin Multiplatform)
- **Kotlin Multiplatform Mobile (KMP)**: Write business logic once, use on both platforms
- **Koin**: Dependency injection framework
- **SQLDelight**: Local database for offline-first data storage
- **Kotlin Coroutines & Flow**: For async operations and reactive data streams
- **Firebase**: 
  - Firebase Auth for user authentication
  - Firestore for cloud sync

### Android
- **Jetpack Compose**: Modern declarative UI framework
- **Material 3**: Latest Material Design components
- **Navigation Compose**: For screen navigation

### iOS
- **SwiftUI**: Apple's declarative UI framework
- **Combine**: For reactive programming (via Kotlin Flow bridges)

## Project Structure

```
JobTrackerApp/
├── shared/                          # Shared Kotlin Multiplatform code
│   ├── src/
│   │   ├── commonMain/             # Shared code for all platforms
│   │   │   ├── domain/              # Business logic, models, use cases
│   │   │   ├── data/               # Data sources (local DB, remote API)
│   │   │   ├── presentation/       # ViewModels and UI state
│   │   │   └── di/                 # Dependency injection setup
│   │   ├── androidMain/            # Android-specific implementations
│   │   └── iosMain/                # iOS-specific implementations
│   └── build.gradle.kts
│
├── androidApp/                     # Android application
│   ├── src/main/
│   │   ├── kotlin/                 # Android UI screens (Compose)
│   │   └── res/                    # Android resources
│   └── build.gradle.kts
│
└── iosApp/                         # iOS application
    ├── iosApp/                     # SwiftUI views and app code
    └── Podfile                     # CocoaPods dependencies
```

## How It Works

### Architecture

The app follows a **clean architecture** pattern:

1. **Domain Layer** (`shared/src/commonMain/kotlin/domain/`)
   - Pure Kotlin business logic
   - Models (Application, Task, Interview, etc.)
   - Use cases (AddApplication, UpdateApplication, etc.)
   - Repository interfaces

2. **Data Layer** (`shared/src/commonMain/kotlin/data/`)
   - Local data source: SQLDelight database
   - Remote data source: Firebase Firestore
   - Repository implementations that combine both sources

3. **Presentation Layer**
   - **Android**: Compose screens + ViewModels
   - **iOS**: SwiftUI views + ViewModels (bridged from Kotlin)

4. **Platform-Specific Code**
   - **Android**: Database driver factory, Firebase setup
   - **iOS**: Database driver factory, Firebase setup, Swift-Kotlin bridges

### Data Flow

```
User Action → View → ViewModel → Repository → Data Source
                                      ↓
                              (Local DB or Firebase)
                                      ↓
                              ViewModel updates StateFlow
                                      ↓
                              View observes and updates UI
```

## Prerequisites

### For Android Development
- **Android Studio** (Hedgehog or later recommended)
- **JDK 17** or higher
- **Android SDK** (API level 24+)
- **Google Services**: You'll need a `google-services.json` file (already included)

### For iOS Development
- **Xcode 15+**
- **CocoaPods**: `sudo gem install cocoapods`
- **macOS** (required for iOS development)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd JobTrackerApp
```

### 2. Android Setup

1. Open the project in **Android Studio**
2. Wait for Gradle sync to complete (it will download dependencies)
3. The `google-services.json` file is already in `androidApp/` directory
4. Sync project: **File → Sync Project with Gradle Files**

### 3. iOS Setup

1. Navigate to the iOS directory:
   ```bash
   cd iosApp
   ```

2. Install CocoaPods dependencies:
   ```bash
   pod install
   ```

3. Open the workspace (NOT the project file):
   ```bash
   open iosApp.xcworkspace
   ```

4. In Xcode, select your target and add `libsqlite3.tbd`:
   - Select your target → **Build Phases** → **Link Binary With Libraries**
   - Click **+** and add `libsqlite3.tbd`

## How to Run

### Running Android App

1. **Open in Android Studio**
2. **Connect a device** or start an emulator
3. Click the **Run** button (green play icon) or press `Shift + F10`
4. Select your device/emulator
5. The app will build and launch

**Or via command line:**
```bash
./gradlew :androidApp:installDebug
adb shell am start -n com.jobtracker.android/.MainActivity
```

### Running iOS App

1. **Open `iosApp/iosApp.xcworkspace` in Xcode** (important: use `.xcworkspace`, not `.xcodeproj`)
2. **Select a simulator** or connect a physical device
3. Click the **Run** button (play icon) or press `Cmd + R`
4. Wait for the build to complete
5. The app will launch in the simulator/device

**Note**: First build might take a while as it compiles the Kotlin shared module.

## Building the Shared Module

The shared Kotlin module needs to be built before iOS can use it:

```bash
# Build shared module for iOS
./gradlew :shared:embedAndSignAppleFrameworkForXcode

# Or build everything
./gradlew build
```

## Features

### Current Features
- Add, edit, and delete job applications
- Filter applications by status (Applied, Interview, Offer, Rejected)
-  Search applications by company or role
-  Add tasks to applications
-  Track interview details
-  Local database (works offline)
-  Firebase authentication (sign in/sign up)
-  Cloud sync with Firestore

### Application Statuses
- **Draft**: Application not yet submitted
- **Applied**: Application submitted
- **Interview**: Interview scheduled/in progress
- **Offer**: Received job offer
- **Rejected**: Application rejected

## Troubleshooting

### Android Issues

**Build fails with Firebase errors:**
- Make sure `google-services.json` is in `androidApp/` directory
- Clean and rebuild: **Build → Clean Project**, then **Build → Rebuild Project**

**Gradle sync fails:**
- Check your internet connection (dependencies need to be downloaded)
- Try: **File → Invalidate Caches / Restart**

### iOS Issues

**Build fails with "Undefined symbol: _sqlite3_*":**
- Add `libsqlite3.tbd` in Xcode: Target → Build Phases → Link Binary With Libraries

**"No such module 'shared'":**
- Run `pod install` in the `iosApp/` directory
- Make sure you're opening `.xcworkspace`, not `.xcodeproj`

**Kotlin compilation errors:**
- Build the shared module first: `./gradlew :shared:build`
- In Xcode, clean build folder: **Product → Clean Build Folder** (`Cmd + Shift + K`)

### General Issues

**Firebase not working:**
- Check that `google-services.json` (Android) and `GoogleService-Info.plist` (iOS) are present
- Verify Firebase project is set up correctly in Firebase Console

**Data not syncing:**
- Make sure you're signed in (check Settings screen)
- Check internet connection
- Verify Firebase rules allow read/write for authenticated users

## Development Notes

### Adding New Features

1. **Add domain model** in `shared/src/commonMain/kotlin/domain/model/`
2. **Create use case** in `shared/src/commonMain/kotlin/domain/usecase/`
3. **Add repository method** in `shared/src/commonMain/kotlin/domain/repository/`
4. **Implement data source** in `shared/src/commonMain/kotlin/data/`
5. **Create ViewModel** in `shared/src/commonMain/kotlin/presentation/viewmodel/`
6. **Add UI**:
   - Android: Compose screen in `androidApp/src/main/kotlin/`
   - iOS: SwiftUI view in `iosApp/iosApp/`

### Database Migrations

SQLDelight migrations go in `shared/src/commonMain/sqldelight/migrations/`. The database schema is defined in `shared/src/commonMain/sqldelight/com/jobtracker/shared/database/JobTrackerDatabase.sq`.


## Contact

[ankitac862@gmail.com]

---

