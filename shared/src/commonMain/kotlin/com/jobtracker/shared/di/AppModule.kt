package com.jobtracker.shared.di

import com.jobtracker.shared.data.local.ApplicationLocalDataSource
import com.jobtracker.shared.data.local.ContactLocalDataSource
import com.jobtracker.shared.data.local.InterviewLocalDataSource
import com.jobtracker.shared.data.local.StatusHistoryLocalDataSource
import com.jobtracker.shared.data.local.TaskLocalDataSource
import com.jobtracker.shared.data.remote.FirebaseAuthWrapper
import com.jobtracker.shared.data.remote.FirestoreWrapper
import com.jobtracker.shared.data.sync.SyncCoordinator
import com.jobtracker.shared.data.repository.ApplicationRepositoryImpl
import com.jobtracker.shared.data.repository.ContactRepositoryImpl
import com.jobtracker.shared.data.repository.InterviewRepositoryImpl
import com.jobtracker.shared.data.repository.StatusHistoryRepositoryImpl
import com.jobtracker.shared.data.repository.TaskRepositoryImpl
import com.jobtracker.shared.database.JobTrackerDatabase
import com.jobtracker.shared.domain.repository.ApplicationRepository
import com.jobtracker.shared.domain.repository.ContactRepository
import com.jobtracker.shared.domain.repository.InterviewRepository
import com.jobtracker.shared.domain.repository.StatusHistoryRepository
import com.jobtracker.shared.domain.repository.TaskRepository
import com.jobtracker.shared.domain.usecase.AddApplication
import com.jobtracker.shared.domain.usecase.AddContact
import com.jobtracker.shared.domain.usecase.AddStatusHistory
import com.jobtracker.shared.domain.usecase.AddTask
import com.jobtracker.shared.domain.usecase.DeleteApplication
import com.jobtracker.shared.domain.usecase.DeleteContact
import com.jobtracker.shared.domain.usecase.ToggleTaskDone
import com.jobtracker.shared.domain.usecase.UpdateApplication
import com.jobtracker.shared.platform.DatabaseDriverFactory
import com.jobtracker.shared.presentation.viewmodel.ApplicationDetailViewModel
import com.jobtracker.shared.presentation.viewmodel.ApplicationsViewModel
import com.jobtracker.shared.presentation.viewmodel.SettingsViewModel
import com.jobtracker.shared.util.TimeProvider
import com.jobtracker.shared.util.UuidProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.qualifier.named

expect fun getTimeProvider(): TimeProvider
expect fun getUuidProvider(): UuidProvider
expect fun getFirebaseAuthWrapper(): FirebaseAuthWrapper
expect fun getFirestoreWrapper(): FirestoreWrapper

val appModule = module {
    single { getTimeProvider() }
    single { getUuidProvider() }
    
    single<JobTrackerDatabase> {
        val driverFactory: DatabaseDriverFactory = get()
        JobTrackerDatabase(driverFactory.createDriver())
    }
    
    single { getFirebaseAuthWrapper() }
    single { getFirestoreWrapper() }
    
    single {
        SyncCoordinator(
            applicationLocalDataSource = get(),
            taskLocalDataSource = get(),
            interviewLocalDataSource = get(),
            contactLocalDataSource = get(),
            statusHistoryLocalDataSource = get(),
            firestoreWrapper = get(),
            firebaseAuthWrapper = get(),
            timeProvider = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        )
    }
    
    single {
        ApplicationLocalDataSource(database = get())
    }
    
    single {
        StatusHistoryLocalDataSource(database = get())
    }
    
    single {
        TaskLocalDataSource(database = get())
    }

    single {
        InterviewLocalDataSource(database = get())
    }

    single {
        ContactLocalDataSource(database = get())
    }
    
    single<ApplicationRepository> {
        ApplicationRepositoryImpl(
            localDataSource = get(),
            statusHistoryDataSource = get(),
            timeProvider = get(),
            uuidProvider = get()
        )
    }
    
    single<StatusHistoryRepository> {
        StatusHistoryRepositoryImpl(
            localDataSource = get()
        )
    }
    
    single<TaskRepository> {
        TaskRepositoryImpl(
            localDataSource = get(),
            timeProvider = get()
        )
    }

    single<InterviewRepository> {
        InterviewRepositoryImpl(
            localDataSource = get(),
            timeProvider = get()
        )
    }

    single<ContactRepository> {
        ContactRepositoryImpl(
            localDataSource = get(),
            timeProvider = get()
        )
    }
    
    factoryOf(::AddApplication)
    factoryOf(::UpdateApplication)
    factoryOf(::DeleteApplication)
    factoryOf(::AddTask)
    factoryOf(::ToggleTaskDone)
    factoryOf(::AddStatusHistory)
    factoryOf(::AddContact)
    factoryOf(::DeleteContact)
    
    factory<ApplicationsViewModel> {
        ApplicationsViewModel(
            applicationRepository = get(),
            addApplication = get(),
            updateApplication = get(),
            deleteApplication = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        )
    }
    
    factory<ApplicationDetailViewModel> {
        ApplicationDetailViewModel(
            applicationRepository = get(),
            statusHistoryRepository = get(),
            taskRepository = get(),
            interviewRepository = get(),
            contactRepository = get(),
            updateApplication = get(),
            addStatusHistory = get(),
            addTask = get(),
            toggleTaskDone = get(),
            addContact = get(),
            deleteContact = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        )
    }
    
    factory<SettingsViewModel> {
        SettingsViewModel(
            syncCoordinator = get(),
            firebaseAuthWrapper = get(),
            coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        )
    }
}

