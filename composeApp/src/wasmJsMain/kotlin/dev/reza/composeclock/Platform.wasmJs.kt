package dev.reza.composeclock

import kotlinx.datetime.TimeZone

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    
    override fun getCurrentTimeZone(): TimeZone {
        return try {
            // For WASM/Web, we can use the browser's timezone
            // This will use the system's timezone through the browser
            TimeZone.currentSystemDefault()
        } catch (e: Exception) {
            // Fallback to UTC if there's an issue
            TimeZone.UTC
        }
    }
}

actual fun getPlatform(): Platform = WasmPlatform()