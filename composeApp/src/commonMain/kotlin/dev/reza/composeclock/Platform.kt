package dev.reza.composeclock

import kotlinx.datetime.TimeZone

interface Platform {
    val name: String
    fun getCurrentTimeZone(): TimeZone
}

expect fun getPlatform(): Platform