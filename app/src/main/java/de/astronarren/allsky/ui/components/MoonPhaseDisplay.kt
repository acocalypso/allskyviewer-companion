package de.astronarren.allsky.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.astronarren.allsky.utils.MoonPhase
import de.astronarren.allsky.utils.MoonPhaseCalculator
import kotlin.math.roundToInt

@Composable
fun MoonPhaseDisplay() {
    val moonPhase = remember { MoonPhaseCalculator.calculateMoonPhase() }
    val illumination = remember { MoonPhaseCalculator.getIllumination() }
    val daysUntilNewMoon = remember { MoonPhaseCalculator.getDaysUntilNewMoon() }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Moon Icon with theme-aware gradient background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = moonPhase.icon,
                    style = MaterialTheme.typography.displayLarge
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Moon Phase Name
            Text(
                text = moonPhase.label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Days until new moon
            Text(
                text = "Next new moon in ${daysUntilNewMoon.roundToInt()} days",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Illumination Percentage
            Text(
                text = "Illumination: ${String.format("%.1f", illumination)}%",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Progress Indicator
            LinearProgressIndicator(
                progress = (illumination / 100).toFloat(),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
} 