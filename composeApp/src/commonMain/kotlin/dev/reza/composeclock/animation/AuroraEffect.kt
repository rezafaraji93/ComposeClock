package dev.reza.composeclock.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import dev.reza.composeclock.model.AnimationType
import kotlin.math.PI
import kotlin.math.sin

class AuroraEffect : AnimationEffect {

    override val animationType = AnimationType.AURORA

    private var phaseProvider: (() -> Float)? = null
    private var burst: Animatable<Float, *>? = null

    private data class Band(
        val radiusOffset: Dp,
        val stroke: Dp,
        val alpha: Float,
        val hueShiftDeg: Float
    )

    private var bands: List<Band> = emptyList()

    @Composable
    override fun initializeAnimations() {
        val transition = rememberInfiniteTransition(label = "aurora-phase")
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 16000, easing = LinearEasing)
            ),
            label = "phase"
        )
        phaseProvider = { phase }

        burst = remember { Animatable(0f) }

        val baseStroke = 3.dp
        val inner = 14.dp
        val step = 7.dp

        bands = List(7) { i ->
            Band(
                radiusOffset = inner + i * step,
                stroke = baseStroke - (0.25.dp * i),
                alpha = (0.42f - i * 0.05f).coerceAtLeast(0.06f),
                hueShiftDeg = i * 28f
            )
        }
    }

    override fun drawEffect(
        drawScope: DrawScope,
        center: Offset,
        radius: Float
    ) = with(drawScope) {
        val phase = phaseProvider?.invoke() ?: return
        val burstAmt = burst?.value ?: 0f

        val amplitude = 0.85f +
                0.10f * sin((phase + 45f) * (PI / 180f)).toFloat() +
                0.35f * burstAmt

        // soft glow layer
        val glowBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF00FFC6).copy(alpha = 0.10f * (0.6f + burstAmt * 0.8f)),
                Color.Transparent
            ),
            center = center,
            radius = radius * (1.25f + 0.15f * burstAmt)
        )
        drawCircle(
            brush = glowBrush,
            radius = radius * (1.25f + 0.15f * burstAmt),
            center = center,
            blendMode = BlendMode.Plus
        )

        bands.forEachIndexed { idx, band ->
            val offsetPx = band.radiusOffset.toPx()
            val strokePx = band.stroke.toPx()
            val wobble = 1f + 0.065f * sin((phase * 2f + band.hueShiftDeg * 3f) * (PI / 180f)).toFloat()
            val r = radius + offsetPx * wobble * amplitude

            val hueOffset = (phase + band.hueShiftDeg) % 360f
            val sweepBrush = Brush.sweepGradient(
                colors = listOf(
                    aurora(160f + hueOffset, band.alpha),
                    aurora(190f + hueOffset, band.alpha * 0.9f),
                    aurora(220f + hueOffset, band.alpha * 0.8f),
                    aurora(280f + hueOffset, band.alpha * 0.6f),
                    aurora(330f + hueOffset, band.alpha * 0.5f),
                    aurora(20f + hueOffset, band.alpha * 0.7f),
                    aurora(80f + hueOffset, band.alpha * 0.9f),
                    aurora(160f + hueOffset, band.alpha)
                ),
                center = center
            )

            drawCircle(
                brush = sweepBrush,
                radius = r,
                center = center,
                style = Stroke(width = strokePx * (1f + burstAmt * 0.4f)),
                blendMode = BlendMode.Plus
            )

            drawCircle(
                brush = sweepBrush,
                radius = r * (0.97f - 0.01f * idx),
                center = center,
                style = Stroke(width = strokePx * 0.55f),
                alpha = 0.65f,
                blendMode = BlendMode.Plus
            )
        }
    }

    override suspend fun triggerAnimation() {
        val b = burst ?: return
        b.stop()
        b.snapTo(0f)
        b.animateTo(1f, tween(260, easing = EaseOutCubic))
        b.animateTo(0f, tween(750, easing = EaseOutCubic))
    }

    private fun aurora(hueDeg: Float, alpha: Float): Color {
        val h = ((hueDeg % 360f) + 360f) % 360f
        return Color.hsv(h, 0.65f, 1.0f, alpha)
    }
}
