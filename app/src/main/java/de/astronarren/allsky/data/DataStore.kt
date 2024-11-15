package de.astronarren.allsky.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Single DataStore instance for the app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "allsky_settings") 