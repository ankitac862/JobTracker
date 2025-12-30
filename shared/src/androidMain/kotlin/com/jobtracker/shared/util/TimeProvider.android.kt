package com.jobtracker.shared.util

actual class TimeProvider {
    actual fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}

