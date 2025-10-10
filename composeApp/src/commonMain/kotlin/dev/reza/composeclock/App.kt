package dev.reza.composeclock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.reza.composeclock.animation.AnimationManager
import dev.reza.composeclock.animation.AuroraEffect
import dev.reza.composeclock.animation.BreathingEffect
import dev.reza.composeclock.animation.CosmicStarsEffect
import dev.reza.composeclock.animation.WaterRipplesEffect
import dev.reza.composeclock.model.AnimationType
import dev.reza.composeclock.ui.AnalogClock
import dev.reza.composeclock.ui.AnimationSelector
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { paddingValues ->
            var selectedAnimation by remember { mutableStateOf(AnimationType.NONE) }

            // Initialize animation manager
            val animationManager = remember {
                val manager = AnimationManager()
                manager.apply {
                    registerEffect(BreathingEffect())
                    registerEffect(AuroraEffect())
                    registerEffect(CosmicStarsEffect())
                    registerEffect(WaterRipplesEffect())
                }

                manager
            }

            // Initialize all animations
            animationManager.initializeAllEffects()

            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Animation selector
                    AnimationSelector(
                        selectedAnimation = selectedAnimation,
                        onAnimationSelected = { selectedAnimation = it })

                    Spacer(modifier = Modifier.height(20.dp))

                    AnalogClock(
                        modifier = Modifier.padding(12.dp).align(Alignment.CenterHorizontally),
                        animationType = selectedAnimation,
                        animationManager = animationManager
                    )
                }
            }
        }
    }

}