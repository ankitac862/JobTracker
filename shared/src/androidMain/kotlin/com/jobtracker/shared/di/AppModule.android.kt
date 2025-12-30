package com.jobtracker.shared.di

import android.content.Context
import com.jobtracker.shared.data.remote.FirebaseAuthWrapper
import com.jobtracker.shared.data.remote.FirestoreWrapper
import com.jobtracker.shared.platform.DatabaseDriverFactory
import com.jobtracker.shared.util.TimeProvider
import com.jobtracker.shared.util.UuidProvider
import org.koin.core.module.Module
import org.koin.dsl.module

fun getAndroidDatabaseDriverFactoryModule(context: Context): Module = module {
    single<DatabaseDriverFactory> {
        DatabaseDriverFactory(context)
    }
}

actual fun getTimeProvider(): TimeProvider = TimeProvider()

actual fun getUuidProvider(): UuidProvider = UuidProvider()

actual fun getFirebaseAuthWrapper(): FirebaseAuthWrapper = FirebaseAuthWrapper()

actual fun getFirestoreWrapper(): FirestoreWrapper = FirestoreWrapper()
