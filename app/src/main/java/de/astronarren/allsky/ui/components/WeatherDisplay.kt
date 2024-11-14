package de.astronarren.allsky.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.astronarren.allsky.data.WeatherData
import de.astronarren.allsky.data.City
import java.text.SimpleDateFormat
import java.util.*
import de.astronarren.allsky.ui.state.WeatherUiState

@Composable
fun WeatherDisplay(
    modifier: Modifier = Modifier,
    uiState: WeatherUiState,
    onRequestPermission: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading weather data...",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                uiState.error?.contains("permission", ignoreCase = true) == true -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Location Permission Required",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Weather forecasts require location permission to provide accurate data for your area.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = onRequestPermission,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Grant Location Permission")
                        }
                    }
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                uiState.weatherData != null -> {
                    val (city, forecasts) = uiState.weatherData
                    
                    // Location Header
                    Text(
                        text = "${city.name}, ${city.country}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "5-Day Weather Forecast",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    // Current Weather
                    forecasts.firstOrNull()?.let { current ->
                        CurrentWeather(current)
                    }
                    
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth()
                    )
                    
                    // 5-Day Forecast
                    Text(
                        text = "Next Days",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(forecasts.take(5)) { dayWeather ->
                            DayForecast(dayWeather)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentWeather(weather: WeatherData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${String.format("%.1f", weather.main.temp)}°C",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = weather.weather.firstOrNull()?.description?.capitalize() ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherInfo(
                label = "Feels like",
                value = "${String.format("%.1f", weather.main.feels_like)}°C"
            )
            WeatherInfo(
                label = "Humidity",
                value = "${weather.main.humidity}%"
            )
            WeatherInfo(
                label = "Cloud Cover",
                value = "${weather.clouds.all}%"
            )
        }
    }
}

@Composable
private fun DayForecast(weather: WeatherData) {
    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatDay(weather.dt),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "${String.format("%.1f", weather.main.temp)}°C",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${weather.clouds.all}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeatherInfo(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun formatDay(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
} 