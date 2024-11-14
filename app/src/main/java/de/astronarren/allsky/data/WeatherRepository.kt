package de.astronarren.allsky.data

import de.astronarren.allsky.utils.LocationManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository(
    private val userPreferences: UserPreferences,
    private val locationManager: LocationManager
) {
    private val weatherService = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherService::class.java)

    suspend fun getForecast(): Result<WeatherResponse> {
        return try {
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