package dev.reza.composeclock.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import dev.reza.composeclock.model.AnimationType

class AnimationManager {
    private val effects = LinkedHashMap<AnimationType, AnimationEffect>()

    fun registerEffect(effect: AnimationEffect) {
        effects[effect.animationType] = effect
    }

    fun unregisterEffect(type: AnimationType) {
        effects.remove(type)
    }

    fun getEffect(type: AnimationType): AnimationEffect? = effects[type]

    @Composable
    fun initializeAllEffects() {
        effects.values.forEach { it.initializeAnimations() }
    }

    fun drawEffect(
        type: AnimationType,
        drawScope: DrawScope,
        center: Offset,
        radius: Float
    ) {
        effects[type]?.drawEffect(drawScope, center, radius)
    }

    suspend fun triggerAnimation(type: AnimationType) {
        effects[type]?.triggerAnimation()
    }
}
