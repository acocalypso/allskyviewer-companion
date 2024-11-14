package de.astronarren.allsky.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {
    companion object {
        private val SETUP_COMPLETE = booleanPreferencesKey("setup_complete")
        private val API_KEY = stringPreferencesKey("api_key")
        private val ALLSKY_URL = stringPreferencesKey("allsky_url")
        private val LAST_WEATHER_DATA = stringPreferencesKey("last_weather_data")
        private val LAST_WEATHER_TIME = stringPreferencesKey("last_weather_time")
    }

    suspend fun isSetupComplete(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[SETUP_COMPLETE] ?: false
        }.first()
    }

    suspend fun markSetupComplete() {
        context.dataStore.edit { preferences ->
            preferences[SETUP_COMPLETE] = true
        }
    }

    suspend fun getAllskyUrl(): String {
        return context.dataStore.data.map { preferences ->
            preferences[ALLSKY_URL] ?: ""
        }.first()
    }

    suspend fun saveAllskyUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[ALLSKY_URL] = url
        }
    }

    suspend fun saveApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }

    suspend fun getApiKey(): String {
        return context.dataStore.data.map { preferences ->
            preferences[API_KEY] ?: ""
        }.first()
    }

    suspend fun saveWeatherData(data: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_WEATHER_DATA] = data
            preferences[LAST_WEATHER_TIME] = System.currentTimeMillis().toString()
        }
    }

    suspend fun getLastWeatherData(): Pair<String?, Long> {
        return context.dataStore.data.map { preferences ->
            Pair(
                preferences[LAST_WEATHER_DATA],
                preferences[LAST_WEATHER_TIME]?.toLongOrNull() ?: 0L
            )
        }.first()
    }

    fun getAllskyUrlFlow(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[ALLSKY_URL] ?: ""
        }
    }

    fun getApiKeyFlow(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[API_KEY] ?: ""
        }
    }
} 