package com.jobtracker.shared.di

import com.jobtracker.shared.data.remote.FirebaseAuthWrapper
import com.jobtracker.shared.data.remote.FirestoreWrapper
import com.jobtracker.shared.platform.DatabaseDriverFactory
import com.jobtracker.shared.util.TimeProvider
import com.jobtracker.shared.util.UuidProvider
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun getIosDatabaseDriverFactoryModule(): Module = module {
    single<DatabaseDriverFactory> {
        DatabaseDriverFactory()
    }
}

// iOS-specific initialization function that includes DatabaseDriverFactory
fun initKoinIos() {
    startKoin {
        modules(
            getIosDatabaseDriverFactoryModule(),
            appModule
        )
    }
}

actual fun getTimeProvider(): TimeProvider = TimeProvider()

actual fun getUuidProvider(): UuidProvider = UuidProvider()

actual fun getFirebaseAuthWrapper(): FirebaseAuthWrapper = FirebaseAuthWrapper()

actual fun getFirestoreWrapper(): FirestoreWrapper = FirestoreWrapper()
