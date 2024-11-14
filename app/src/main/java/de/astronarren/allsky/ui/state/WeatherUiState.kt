package de.astronarren.allsky.ui.state

import de.astronarren.allsky.data.City
import de.astronarren.allsky.data.WeatherData

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherData: Pair<City, List<WeatherData>>? = null,
    val error: String? = null
) 