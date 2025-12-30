package com.jobtracker.shared.util

import java.util.UUID

actual class UuidProvider {
    actual fun generate(): String {
        return UUID.randomUUID().toString()
    }
}

