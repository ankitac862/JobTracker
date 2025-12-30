package com.jobtracker.android

import android.app.Application
import com.jobtracker.shared.di.appModule
import com.jobtracker.shared.di.getAndroidDatabaseDriverFactoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class JobTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@JobTrackerApplication)
            modules(
                getAndroidDatabaseDriverFactoryModule(this@JobTrackerApplication),
                appModule
            )
        }
    }
}

