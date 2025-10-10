package dev.reza.composeclock.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import dev.reza.composeclock.model.AnimationType

interface AnimationEffect {
    val animationType: AnimationType
    
    @Composable
    fun initializeAnimations()
    
    fun drawEffect(
        drawScope: DrawScope,
        center: Offset,
        radius: Float
    )
    
    suspend fun triggerAnimation()
}
