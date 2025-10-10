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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class CosmicStarsEffect : AnimationEffect {

    override val animationType = AnimationType.COSMIC_STARS

    private var phaseProvider: (() -> Float)? = null

    private var burst: Animatable<Float, *>? = null

    private var stars: List<Star> = emptyList()

    private val starPalette = listOf(
        Color(0xFF0044FF),
        Color(0xFF00AACC),
        Color(0xFF7722FF),
        Color(0xFF5555FF),
        Color(0xFF008866),
        Color(0xFF222222)
    )

    private data class Star(
        val baseAngleDeg: Float,
        val radiusDp: Dp,
        val sizeDp: Dp,
        val color: Color,
        val layer: Int, // 0: far, 1: mid, 2: near
        val twinkleOffsetDeg: Float
    )

    @Composable
    override fun initializeAnimations() {
        val transition = rememberInfiniteTransition(label = "cosmic-phase")
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 24_000, easing = LinearEasing)
            ),
            label = "phase"
        )
        phaseProvider = { phase }

        burst = remember { Animatable(0f) }

        val rng = remember { Random(1337) }
        stars = remember {
            val count = 140
            List(count) { i ->
                val layer = when {
                    i % 7 == 0 -> 2  // near (bigger, brighter, faster)
                    i % 3 == 0 -> 1  // mid
                    else -> 0        // far
                }

                val radiusDp = when (layer) {
                    2 -> ((26 + rng.nextInt(14)) * 1.5f).dp
                    1 -> ((36 + rng.nextInt(28)) * 1.25f).dp
                    else -> (48 + rng.nextInt(56)).dp
                }

                val sizeDp = when (layer) {
                    2 -> (1.6f + rng.nextFloat() * 1.3f).dp
                    1 -> (1.1f + rng.nextFloat()).dp
                    else -> (0.8f + rng.nextFloat() * 0.9f).dp
                }

                val color = starPalette[rng.nextInt(starPalette.size)]

                Star(
                    baseAngleDeg = rng.nextFloat() * 360f,
                    radiusDp = radiusDp,
                    sizeDp = sizeDp,
                    color = color,
                    layer = layer,
                    twinkleOffsetDeg = rng.nextFloat() * 360f
                )
            }
        }
    }

    override fun drawEffect(
        drawScope: DrawScope,
        center: Offset,
        radius: Float
    ) = with(drawScope) {
        val phase = phaseProvider?.invoke() ?: return
        val burstAmt = burst?.value ?: 0f

        val layerSpeed = floatArrayOf(0.12f, 0.35f, 0.85f)

        val globalAlphaBoost = 1f + burstAmt * 0.9f
        val radiusBoost = 1f + burstAmt * 0.08f

        val sky = Brush.radialGradient(
            colors = listOf(
                Color(0xFF000000).copy(alpha = 0.08f * (0.7f + 0.3f * globalAlphaBoost)),
                Color.Transparent
            ),
            center = center,
            radius = radius * 1.35f * radiusBoost
        )
        drawCircle(
            brush = sky,
            radius = radius * 1.35f * radiusBoost,
            center = center,
            blendMode = BlendMode.SrcOver
        )

        for (s in stars) {
            val angle = (s.baseAngleDeg + phase * layerSpeed[s.layer]) % 360f
            val r = radius + s.radiusDp.toPx() * radiusBoost // orbit outside/around the clock face
            val angRad = angle * (PI / 180f).toFloat()

            val x = center.x + cos(angRad) * r
            val y = center.y + sin(angRad) * r

            val twinklePhase = (phase * (1.8f + 0.4f * s.layer) + s.twinkleOffsetDeg) % 360f
            val twinkle = (0.55f + 0.45f * (0.5f + 0.5f * sin(twinklePhase * PI / 180f))).toFloat()

            val sizePx = s.sizeDp.toPx()
            val layerAlphaFactor = when (s.layer) { 2 -> 1.0f; 1 -> 0.85f; else -> 0.7f }

            val coreAlpha = (0.28f + 0.48f * twinkle) * layerAlphaFactor * globalAlphaBoost
            val glowAlpha = coreAlpha * 0.36f

            drawCircle(
                color = Color.Black.copy(alpha = (coreAlpha * 0.25f).coerceIn(0f, 0.25f)),
                radius = sizePx + 1.2f,
                center = Offset(x, y),
                blendMode = BlendMode.SrcOver
            )

            drawCircle(
                color = s.color.copy(alpha = coreAlpha.coerceIn(0f, 1f)),
                radius = sizePx,
                center = Offset(x, y),
                blendMode = BlendMode.SrcOver
            )

            drawCircle(
                color = s.color.copy(alpha = glowAlpha.coerceIn(0f, 1f)),
                radius = sizePx * (2.2f + 0.8f * twinkle),
                center = Offset(x, y),
                blendMode = BlendMode.Plus
            )
        }

        drawCircle(
            color = Color.Black.copy(alpha = 0.035f * (1f + burstAmt * 0.5f)),
            radius = radius * 1.55f * radiusBoost,
            center = center,
            style = Stroke(width = 0.75f.dp.toPx()),
            blendMode = BlendMode.SrcOver
        )
    }

    override suspend fun triggerAnimation() {
        val b = burst ?: return
        b.stop()
        b.snapTo(0f)
        b.animateTo(1f, tween(220, easing = EaseOutCubic))
        b.animateTo(0f, tween(700, easing = LinearEasing))
    }
}
