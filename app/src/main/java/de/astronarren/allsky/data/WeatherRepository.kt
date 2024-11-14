package de.astronarren.allsky.data

import de.astronarren.allsky.utils.LocationManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository(
    private val locationManager: LocationManager,
    private val weatherService: WeatherService,
    private val userPreferences: UserPreferences
) {
    suspend fun getForecast(): Result<WeatherResponse> {
        return try {
            if (!locationManager.isLocationPermissionGranted()) {
                return Result.failure(Exception("Location permission required"))
            }
            
            val location = locationManager.getCurrentLocation() ?: 
                return Result.failure(Exception("Location not available"))
            
            val apiKey = userPreferences.getApiKey()
            if (apiKey.isEmpty()) {
                return Result.failure(Exception("API key not configured"))
            }

            val response = weatherService.getForecast(
                lat = location.latitude,
                lon = location.longitude,
                apiKey = apiKey
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 