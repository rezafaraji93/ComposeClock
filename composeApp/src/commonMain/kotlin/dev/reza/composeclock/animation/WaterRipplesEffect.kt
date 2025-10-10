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
import dev.reza.composeclock.model.AnimationType
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class WaterRipplesEffect : AnimationEffect {

    override val animationType = AnimationType.WATER_RIPPLES

    private var phaseProvider: (() -> Float)? = null

    private var pulse: Animatable<Float, *>? = null

    private var bands: List<Band> = emptyList()

    private data class Band(
        val offset: Dp,      // distance from base radius
        val stroke: Dp,      // line width
        val alpha: Float,    // base opacity
        val lag: Float       // per-band phase lag (0..1)
    )

    @Composable
    override fun initializeAnimations() {
        val t = rememberInfiniteTransition(label = "ripples-phase")
        val phase by t.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 6000, easing = LinearEasing)
            ),
            label = "phase"
        )
        phaseProvider = { phase }

        pulse = remember { Animatable(0f) }

        bands = List(7) { i ->
            val tBand = i / 6f
            Band(
                offset = (10f + 12f * i).dp,
                stroke = (2.2f - 1.2f * tBand).coerceAtLeast(0.8f).dp,
                alpha = (0.36f - 0.045f * i).coerceAtLeast(0.05f),
                lag = tBand * 0.65f // outer rings lag a bit more
            )
        }
    }

    override fun drawEffect(
        drawScope: DrawScope,
        center: Offset,
        radius: Float
    ) = with(drawScope) {
        val phase = phaseProvider?.invoke() ?: return
        val splash = pulse?.value ?: 0f

        // ---------- Base water body ----------
        val baseR = radius * (0.68f + 0.16f * splash)
        val waterBody = Brush.radialGradient(
            colors = listOf(
                Color(0xFF38BDF8).copy(alpha = 0.18f + 0.12f * splash),
                Color(0xFF0EA5E9).copy(alpha = 0.10f + 0.10f * splash),
                Color.Transparent
            ),
            center = center,
            radius = baseR * 1.25f
        )
        drawCircle(brush = waterBody, radius = baseR, center = center, blendMode = BlendMode.SrcOver)

        // Caustic highlight (offset shine)
        val shine = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.12f + 0.12f * splash), Color.Transparent),
            center = center.copy(x = center.x + radius * 0.22f, y = center.y - radius * 0.18f),
            radius = radius * (0.55f + 0.15f * splash)
        )
        drawCircle(brush = shine, radius = radius * (0.50f + 0.12f * splash), center = center, blendMode = BlendMode.Plus)

        // ---------- Moving ripples ----------
        val amp = 0.035f + 0.015f * sin(phase * 2f * PI).toFloat() + 0.08f * splash

        bands.forEachIndexed { idx, band ->
            val strokePx = band.stroke.toPx() * (1f + 0.35f * splash)
            val alpha = (band.alpha * (0.75f + 0.25f * splash)).coerceIn(0f, 1f)

            val age = ((phase - band.lag) % 1f + 1f) % 1f

            val wobble =
                1f +
                        amp * sin((age * 360f + idx * 17f) * PI / 180f).toFloat() +
                        0.02f * cos((age * 720f + idx * 33f) * PI / 180f).toFloat()

            val r = (radius + band.offset.toPx()) * wobble

            val hue = 195f + 12f * sin((age * 360f) * PI / 180f).toFloat()
            val c1 = Color.hsv(((hue % 360f) + 360f) % 360f, 0.55f, 1f, alpha)
            val c2 = Color.hsv(((hue + 18f) % 360f + 360f) % 360f, 0.65f, 1f, alpha * 0.9f)

            drawCircle(
                brush = Brush.sweepGradient(colors = listOf(c1, c2, c1), center = center),
                radius = r,
                center = center,
                style = Stroke(width = strokePx),
                blendMode = BlendMode.Plus
            )

            drawCircle(
                brush = Brush.sweepGradient(colors = listOf(c2, c1, c2), center = center),
                radius = r * (0.965f - idx * 0.006f),
                center = center,
                style = Stroke(width = strokePx * 0.55f),
                alpha = (alpha * 0.8f).coerceIn(0f, 1f),
                blendMode = BlendMode.Plus
            )
        }

        repeat(5) { i ->
            val a = (phase * 360f + i * 72f) * (PI / 180f).toFloat()
            val sparkle = Offset(
                x = center.x + cos(a) * (radius * (0.34f + 0.10f * i)),
                y = center.y + sin(a) * (radius * (0.10f + 0.07f * i))
            )
            val sAlpha = (0.08f + 0.10f * abs(sin((phase * 720f + i * 33f) * PI / 180f))).coerceIn(
                0.0,
                0.2
            )
            drawCircle(
                color = Color.White.copy(alpha = (sAlpha * (1f + 0.8f * splash)).toFloat()),
                radius = (1.2f + 0.3f * i).dp.toPx(),
                center = sparkle,
                blendMode = BlendMode.Plus
            )
        }
    }

    override suspend fun triggerAnimation() {
        val p = pulse ?: return
        p.stop()
        p.snapTo(0f)
        p.animateTo(1f, tween(220, easing = EaseOutCubic))
        p.animateTo(0f, tween(820, easing = LinearEasing))
    }
}
