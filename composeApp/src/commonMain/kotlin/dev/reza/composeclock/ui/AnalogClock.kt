package dev.reza.composeclock.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.reza.composeclock.animation.AnimationManager
import dev.reza.composeclock.model.AnimationType
import kotlinx.coroutines.delay
import kotlinx.datetime.toLocalDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun AnalogClock(
    modifier: Modifier = Modifier,
    animationType: AnimationType = AnimationType.NONE,
    animationManager: AnimationManager
) {
    val platform = dev.reza.composeclock.getPlatform()
    val currentTimeZone = remember { platform.getCurrentTimeZone() }
    
    var currentTime by remember {
        mutableStateOf(
            kotlin.time.Clock.System.now().toLocalDateTime(currentTimeZone)
        )
    }

    val animatedSecondAngle = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(Unit) {
        while (true) {
            currentTime =
                kotlin.time.Clock.System.now().toLocalDateTime(currentTimeZone)
            delay(1000L)
        }
    }

    val hours = currentTime.hour
    val minutes = currentTime.minute
    val seconds = currentTime.second

    val secondAngle = seconds * 6f

    LaunchedEffect(secondAngle) {
        animatedSecondAngle.animateTo(secondAngle, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ))
    }

    LaunchedEffect(seconds, animationType) {
        animationManager.triggerAnimation(animationType)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16L) // ~60 FPS
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            val center = this.center
            val radius = size.minDimension / 2.0f

            animationManager.drawEffect(animationType, this, center, radius)

            for (i in 1..60) {
                val angleInDegrees = i * 6.0 - 90.0

                val angleInRad = (angleInDegrees * (PI / 180.0)).toFloat()
                val isHourMarker = i % 5 == 0
                val lineLength = if (isHourMarker) 20.dp.toPx() else 10.dp.toPx()
                val lineStrokeWidth = if (isHourMarker) 2.dp.toPx() else 1.dp.toPx()

                val start = Offset(
                    x = center.x + (radius - lineLength) * cos(angleInRad),
                    y = center.y + (radius - lineLength) * sin(angleInRad)
                )
                val end = Offset(
                    x = center.x + radius * cos(angleInRad),
                    y = center.y + radius * sin(angleInRad)
                )

                drawLine(
                    color = if (isHourMarker) Color.Green else Color.Black,
                    start = start,
                    end = end,
                    strokeWidth = lineStrokeWidth
                )
            }

            val labelRadius = radius - 36.dp.toPx()

            for (hour in 1..12) {
                val angleDeg = hour * 30f - 90f
                val angleInRad = (angleDeg * (PI / 180.0)).toFloat()

                val labelCenter = Offset(
                    x = center.x + labelRadius * cos(angleInRad),
                    y = center.y + labelRadius * sin(angleInRad)
                )

                val textLayout = textMeasurer.measure(
                    text = AnnotatedString(hour.toString()),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                )

                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        x = labelCenter.x - textLayout.size.width / 2f,
                        y = labelCenter.y - textLayout.size.height / 2f
                    )
                )
            }

            drawRotatedLine(
                angle = animatedSecondAngle.value,
                lineLength = radius * 0.9f,
                strokeWidth = 1.dp.toPx(),
                color = Color.Red
            )

            val minuteAngle = (minutes * 6f) + (seconds * 0.1f)
            drawRotatedLine(
                angle = minuteAngle,
                lineLength = radius * 0.75f,
                strokeWidth = 3.dp.toPx(),
                color = Color.Black
            )

            val hourAngle = ((hours % 12) * 30f) + (minutes * 0.5f)
            drawRotatedLine(
                angle = hourAngle,
                lineLength = radius * 0.5f,
                strokeWidth = 4.dp.toPx(),
                color = Color.Black
            )

            drawCircle(color = Color.Black, radius = 6.dp.toPx(), center = center)
        }
    }
}

private fun DrawScope.drawRotatedLine(
    angle: Float,
    lineLength: Float,
    strokeWidth: Float,
    color: Color
) {

    rotate(degrees = angle, pivot = center) {
        drawLine(
            color = color,
            start = Offset(center.x, center.y - lineLength),
            end = center,
            strokeWidth = strokeWidth
        )
    }
}
