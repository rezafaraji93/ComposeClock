package dev.reza.composeclock

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeClock",
    ) {
        App()
    }
}