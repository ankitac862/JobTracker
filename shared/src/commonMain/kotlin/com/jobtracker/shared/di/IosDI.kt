package com.jobtracker.shared.di

import com.jobtracker.shared.presentation.viewmodel.ApplicationDetailViewModel
import com.jobtracker.shared.presentation.viewmodel.ApplicationsViewModel
import com.jobtracker.shared.presentation.viewmodel.SettingsViewModel
import com.jobtracker.shared.data.remote.FirebaseAuthWrapper
import com.jobtracker.shared.data.sync.SyncCoordinator
import org.koin.core.Koin
import org.koin.mp.KoinPlatform

private fun koin(): Koin = KoinPlatform.getKoin()

fun provideApplicationsViewModel(): ApplicationsViewModel = koin().get()
fun provideApplicationDetailViewModel(): ApplicationDetailViewModel = koin().get()
fun provideSettingsViewModel(): SettingsViewModel = koin().get()

fun provideFirebaseAuthWrapper(): FirebaseAuthWrapper = koin().get()
fun provideSyncCoordinator(): SyncCoordinator = koin().get()