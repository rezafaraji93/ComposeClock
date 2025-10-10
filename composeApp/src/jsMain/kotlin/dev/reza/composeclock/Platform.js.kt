package dev.reza.composeclock

import kotlinx.datetime.TimeZone

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
    
    override fun getCurrentTimeZone(): TimeZone {
        return try {
            // For JS/Web, we can use the browser's timezone
            // This will use the system's timezone through the browser
            TimeZone.currentSystemDefault()
        } catch (e: Exception) {
            // Fallback to UTC if there's an issue
            TimeZone.UTC
        }
    }
}

actual fun getPlatform(): Platform = JsPlatform()