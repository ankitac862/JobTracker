package com.jobtracker.shared.util

expect class TimeProvider {
    fun currentTimeMillis(): Long
}

