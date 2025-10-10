package dev.reza.composeclock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.reza.composeclock.model.AnimationType

@Composable
fun AnimationSelector(
    selectedAnimation: AnimationType,
    onAnimationSelected: (AnimationType) -> Unit
) {
    val animationOptions = AnimationType.entries.filter { it != AnimationType.NONE }
    
    Text(
        text = "Choose Animation:",
        fontSize = 16.sp,
        color = Color.Gray
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    val buttonsPerRow = 3
    val rows = animationOptions.chunked(buttonsPerRow)
    
    rows.forEach { rowAnimations ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowAnimations.forEach { animation ->
                Button(
                    onClick = { onAnimationSelected(animation) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedAnimation == animation) 
                            Color.Blue 
                        else Color.Gray
                    ),
                    modifier = Modifier.size(width = 100.dp, height = 40.dp)
                ) {
                    Text(
                        text = animation.name.replace("_", "\n"),
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}
