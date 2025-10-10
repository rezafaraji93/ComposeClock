package dev.reza.composeclock

import android.os.Build
import java.util.TimeZone as JavaTimeZone
import kotlinx.datetime.TimeZone

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    
    override fun getCurrentTimeZone(): TimeZone {
        return try {
            // Get the system timezone ID and create kotlinx.datetime.TimeZone
            val systemTimeZoneId = JavaTimeZone.getDefault().id
            TimeZone.of(systemTimeZoneId)
        } catch (e: Exception) {
            // Fallback to currentSystemDefault if there's an issue
            TimeZone.currentSystemDefault()
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()