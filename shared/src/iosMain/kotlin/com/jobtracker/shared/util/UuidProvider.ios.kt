package com.jobtracker.shared.util

import platform.Foundation.NSUUID

actual class UuidProvider {
    actual fun generate(): String {
        return NSUUID().UUIDString
    }
}
