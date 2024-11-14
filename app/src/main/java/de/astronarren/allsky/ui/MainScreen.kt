package de.astronarren.allsky.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import de.astronarren.allsky.ui.components.*
import de.astronarren.allsky.data.UserPreferences
import de.astronarren.allsky.viewmodel.*
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userPreferences: UserPreferences,
    weatherViewModel: WeatherViewModel,
    allskyViewModel: AllskyViewModel,
    imageViewerViewModel: ImageViewerViewModel,
    liveImageViewModel: LiveImageViewModel,
    onNavigateToAbout: () -> Unit
) {
    var isSettingsOpen by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    
    // Collect UI states following lifecycle-aware pattern
    val weatherUiState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val allskyUiState by allskyViewModel.uiState.collectAsStateWithLifecycle()
    val imageViewerState by imageViewerViewModel.uiState.collectAsStateWithLifecycle()
    val liveImageState by liveImageViewModel.uiState.collectAsStateWithLifecycle()
    
    var allskyUrl by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        apiKey = userPreferences.getApiKey()
        allskyUrl = userPreferences.getAllskyUrl()
    }

    // Update weather when API key changes
    LaunchedEffect(apiKey) {
        if (apiKey.isNotEmpty()) {
            weatherViewModel.updateWeather()
        }
    }

    // Collect URL changes in a lifecycle-aware manner
    LaunchedEffect(Unit) {
        userPreferences.getAllskyUrlFlow()
            .collect { url ->
                allskyUrl = url
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SettingsPanel(
                isOpen = isSettingsOpen,
                onDismiss = {
                    scope.launch {
                        drawerState.close()
                        isSettingsOpen = false
                    }
                },
                onAboutClick = {
                    scope.launch {
                        drawerState.close()
                        isSettingsOpen = false
                        onNavigateToAbout()
                    }
                },
                userPreferences = userPreferences
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Allsky") },
                    actions = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    isSettingsOpen = true
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Main scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // AllSky Image
                    if (allskyUrl.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = liveImageState.imageUrl,
                                    contentDescription = "Live AllSky Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                
                                // Optional: Show last update time
                                Text(
                                    text = "Last update: ${formatTime(liveImageState.lastUpdate)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(4.dp)
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No Allsky URL configured",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Moon Phase Display
                    MoonPhaseDisplay()
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Weather Display
                    if (apiKey.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Weather Forecast",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Weather data is only available with a valid OpenWeather API key",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                
                                val uriHandler = LocalUriHandler.current
                                TextButton(
                                    onClick = { 
                                        uriHandler.openUri("https://home.openweathermap.org/api_keys")
                                    }
                                ) {
                                    Text("Get API Key")
                                }
                            }
                        }
                    } else {
                        WeatherDisplay(uiState = weatherUiState)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Media sections
                    if (allskyUiState.isLoading) {
                        CircularProgressIndicator()
                    } else if (allskyUiState.error != null) {
                        Text(
                            text = allskyUiState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        AllskyMediaSection(
                            title = "Timelapses",
                            media = allskyUiState.timelapses,
                            onMediaClick = { /* Handle video playback differently */ }
                        )

                        AllskyMediaSection(
                            title = "Keograms",
                            media = allskyUiState.keograms,
                            onMediaClick = { media -> 
                                println("Debug: Keogram clicked: ${media.url}")
                                imageViewerViewModel.showImage(media.url)
                            }
                        )

                        AllskyMediaSection(
                            title = "Startrails",
                            media = allskyUiState.startrails,
                            onMediaClick = { media -> 
                                println("Debug: Startrail clicked: ${media.url}")
                                imageViewerViewModel.showImage(media.url)
                            }
                        )
                    }
                }

                // Full-screen image viewer overlay
                if (imageViewerState.isFullScreen && imageViewerState.currentImageUrl != null) {
                    println("Debug: Showing full screen image: ${imageViewerState.currentImageUrl}")
                    FullScreenImageViewer(
                        imageUrl = imageViewerState.currentImageUrl!!,
                        onDismiss = { imageViewerViewModel.dismissImage() }
                    )
                }
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    return if (timestamp == 0L) {
        ""
    } else {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
} 