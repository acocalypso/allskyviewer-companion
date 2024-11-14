package de.astronarren.allsky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.astronarren.allsky.data.WeatherRepository
import de.astronarren.allsky.data.UserPreferences
import de.astronarren.allsky.ui.state.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.getApiKeyFlow().collect { apiKey ->
                if (apiKey.isNotBlank()) {
                    updateWeather()
                }
            }
        }
    }

    fun updateWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            weatherRepository.getForecast()
                .onSuccess { response ->
                    val dailyForecasts = response.list
                        .groupBy { formatDay(it.dt) }
                        .map { it.value.first() }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            weatherData = Pair(response.city, dailyForecasts)
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error occurred"
                        )
                    }
                }
        }
    }

    private fun formatDay(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
} 