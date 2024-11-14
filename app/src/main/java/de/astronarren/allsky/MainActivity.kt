package de.astronarren.allsky

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import de.astronarren.allsky.data.UserPreferences
import de.astronarren.allsky.data.network.WeatherApiProvider
import de.astronarren.allsky.ui.MainScreen
import de.astronarren.allsky.ui.about.AboutScreen
import de.astronarren.allsky.ui.setup.SetupScreen
import de.astronarren.allsky.ui.theme.AllskyTheme
import de.astronarren.allsky.utils.LocationManager
import de.astronarren.allsky.viewmodel.*
import de.astronarren.allsky.data.WeatherRepository
import de.astronarren.allsky.data.AllskyRepository

class MainActivity : ComponentActivity() {
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only trigger weather update when permission is granted
                weatherViewModel.updateWeather()
            }
        }
    }

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var allskyViewModel: AllskyViewModel
    private lateinit var imageViewerViewModel: ImageViewerViewModel
    private lateinit var setupViewModel: SetupViewModel
    private lateinit var liveImageViewModel: LiveImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val userPreferences = UserPreferences(applicationContext)
        val locationManager = LocationManager(applicationContext)
        
        // Get WeatherService from provider
        val weatherService = WeatherApiProvider.provideWeatherService()
        
        val weatherRepository = WeatherRepository(
            locationManager = locationManager,
            weatherService = weatherService,
            userPreferences = userPreferences
        )
        
        val allskyRepository = AllskyRepository(userPreferences)
        
        weatherViewModel = WeatherViewModel(
            weatherRepository = weatherRepository,
            userPreferences = userPreferences
        )
        allskyViewModel = AllskyViewModel(allskyRepository, userPreferences)
        imageViewerViewModel = ImageViewerViewModel()
        setupViewModel = SetupViewModel(userPreferences)
        liveImageViewModel = LiveImageViewModel(userPreferences)
        
        checkAndRequestLocationPermissions()
        
        enableEdgeToEdge()
        setContent {
            AllskyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSetup by remember { mutableStateOf(true) }
                    var showAbout by remember { mutableStateOf(false) }
                    
                    LaunchedEffect(Unit) {
                        showSetup = !userPreferences.isSetupComplete()
                    }
                    
                    when {
                        showSetup -> {
                            SetupScreen(
                                viewModel = setupViewModel,
                                onSetupComplete = { showSetup = false }
                            )
                        }
                        showAbout -> {
                            AboutScreen(
                                onNavigateBack = { showAbout = false }
                            )
                        }
                        else -> {
                            MainScreen(
                                userPreferences = userPreferences,
                                weatherViewModel = weatherViewModel,
                                allskyViewModel = allskyViewModel,
                                imageViewerViewModel = imageViewerViewModel,
                                liveImageViewModel = liveImageViewModel,
                                onNavigateToAbout = { showAbout = true },
                                onRequestLocationPermission = {
                                    locationPermissionRequest.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                weatherViewModel.updateWeather()
            }
            else -> {
                // Request permissions
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
}