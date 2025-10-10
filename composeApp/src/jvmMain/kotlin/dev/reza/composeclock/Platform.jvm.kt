package dev.reza.composeclock

import java.util.TimeZone as JavaTimeZone
import kotlinx.datetime.TimeZone

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    
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

actual fun getPlatform(): Platform = JVMPlatform()