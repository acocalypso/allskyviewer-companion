package de.astronarren.allsky.data.network

import de.astronarren.allsky.data.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherApiProvider {
    fun provideWeatherService(): WeatherService {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
} 