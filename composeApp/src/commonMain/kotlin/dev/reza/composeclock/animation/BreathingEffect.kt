package dev.reza.composeclock.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.reza.composeclock.model.AnimationType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BreathingEffect : AnimationEffect {

    override val animationType = AnimationType.BREATHING

    private var phaseProvider: (() -> Float)? = null

    private var accent: Animatable<Float, *>? = null

    @Composable
    override fun initializeAnimations() {
        val transition = rememberInfiniteTransition(label = "breathing-phase")
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = (2f * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4200, easing = LinearOutSlowInEasing)
            ),
            label = "phase"
        )
        phaseProvider = { phase }
        accent = remember { Animatable(0f) }
    }

    override fun drawEffect(
        drawScope: DrawScope,
        center: Offset,
        radius: Float
    ) = with(drawScope) {
        val phase = phaseProvider?.invoke() ?: return
        val accentAmt = accent?.value ?: 0f

        val baseScale = 1f + 0.06f * sin(phase)
        val breathAlpha = 0.25f + 0.20f * (0.5f - 0.5f * cos(phase)) // in-phase softening

        val hue = ((180f + 40f * sin(phase * 0.5f)) + 360f) % 360f
        val mainColor = Color.hsv(hue, 0.55f, 1f)
        val edgeColor = Color.hsv((hue + 25f) % 360f, 0.65f, 1f)

        val innerGlow = Brush.radialGradient(
            colors = listOf(
                mainColor.copy(alpha = 0.12f + breathAlpha * 0.15f),
                Color.Transparent
            ),
            center = center,
            radius = (radius * 1.1f * (1f + 0.05f * sin(phase + PI / 2f))).toFloat()
        )
        drawCircle(
            brush = innerGlow,
            radius = radius * 1.1f * baseScale,
            center = center,
            blendMode = BlendMode.Plus
        )

        drawCircle(
            color = mainColor.copy(alpha = 0.08f + breathAlpha * 0.18f),
            radius = radius * baseScale,
            center = center,
            blendMode = BlendMode.SrcOver
        )

        repeat(3) { i ->
            val idx = i + 1
            val ringScale = baseScale * (1f + idx * 0.06f)
            val widthPx = (2.5f - i * 0.5f).dp.toPx()
            val ringAlpha = (0.22f - i * 0.06f) * (0.7f + 0.3f * breathAlpha)

            drawCircle(
                color = edgeColor.copy(alpha = ringAlpha.coerceAtLeast(0.04f)),
                radius = radius * ringScale,
                center = center,
                style = Stroke(width = widthPx),
                blendMode = BlendMode.Plus
            )
        }

        if (accentAmt > 0.001f) {
            val rippleRadius = radius * (1.0f + 0.5f * accentAmt)
            val rippleAlpha = (1f - accentAmt).let { it * it } * 0.45f

            drawCircle(
                color = edgeColor.copy(alpha = rippleAlpha),
                radius = rippleRadius,
                center = center,
                style = Stroke(width = (3f + 6f * (1f - accentAmt)).dp.toPx()),
                blendMode = BlendMode.Plus
            )
        }
    }

    override suspend fun triggerAnimation() {
        val a = accent ?: return
        a.stop()
        a.snapTo(0f)
        a.animateTo(1f, tween(240, easing = EaseOutCubic))
        a.animateTo(0f, tween(820, easing = LinearOutSlowInEasing))
    }
}
