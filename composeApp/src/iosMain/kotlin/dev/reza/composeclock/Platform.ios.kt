package dev.reza.composeclock

import platform.UIKit.UIDevice
import platform.Foundation.NSTimeZone
import kotlinx.datetime.TimeZone
import platform.Foundation.defaultTimeZone

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    
    override fun getCurrentTimeZone(): TimeZone {
        return try {
            // Get the system timezone name from NSTimeZone
            val systemTimeZoneName = NSTimeZone.defaultTimeZone.name
            TimeZone.of(systemTimeZoneName)
        } catch (e: Exception) {
            // Fallback to currentSystemDefault if there's an issue
            TimeZone.currentSystemDefault()
        }
    }
}

actual fun getPlatform(): Platform = IOSPlatform()