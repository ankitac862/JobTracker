package com.jobtracker.shared.util

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual class TimeProvider {
    actual fun currentTimeMillis(): Long {
        // NSDate().timeIntervalSince1970 returns seconds since 1970
        // Multiply by 1000 to convert to milliseconds
        val date = NSDate()
        return (date.timeIntervalSince1970 * 1000.0).toLong()
    }
}
