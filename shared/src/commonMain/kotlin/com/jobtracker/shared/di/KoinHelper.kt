package com.jobtracker.shared.di

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

fun doInitKoin() {
    startKoin {
        modules(appModule)
    }
}

fun getKoin(): Koin {
    return KoinPlatform.getKoin()
}

