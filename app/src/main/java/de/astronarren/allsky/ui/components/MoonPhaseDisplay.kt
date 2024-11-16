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
import androidx.compose.ui.res.stringResource
import de.astronarren.allsky.R
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
                    text = moonPhase.emoji,
                    style = MaterialTheme.typography.displayLarge
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Moon Phase Name
            Text(
                text = stringResource(R.string.moon_phase),
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "${moonPhase.emoji} ${stringResource(moonPhase.stringResId)}",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Days until new moon
            Text(
                text = stringResource(R.string.next_new_moon_in, daysUntilNewMoon.roundToInt()),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Illumination Percentage
            Text(
                text = stringResource(R.string.moon_illumination),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Add illumination percentage text
            Text(
                text = "%.1f%%".format(illumination),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            // Progress Indicator
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(4.dp),
                progress = { (illumination / 100).toFloat() },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
} 