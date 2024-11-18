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
import androidx.compose.ui.res.stringResource
import de.astronarren.allsky.R
import de.astronarren.allsky.utils.LanguageManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userPreferences: UserPreferences,
    weatherViewModel: WeatherViewModel,
    allskyViewModel: AllskyViewModel,
    imageViewerViewModel: ImageViewerViewModel,
    liveImageViewModel: LiveImageViewModel,
    languageManager: LanguageManager,
    onNavigateToAbout: () -> Unit,
    onRequestLocationPermission: () -> Unit
) {
    var isSettingsOpen by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    
    val weatherUiState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val allskyUiState by allskyViewModel.uiState.collectAsStateWithLifecycle()
    val imageViewerState by imageViewerViewModel.uiState.collectAsStateWithLifecycle()
    val liveImageState by liveImageViewModel.uiState.collectAsStateWithLifecycle()
    
    var allskyUrl by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val updateViewModel: UpdateViewModel = viewModel(
        factory = UpdateViewModelFactory(context.applicationContext as android.app.Application)
    )
    val updateState by updateViewModel.uiState.collectAsState()
    val showUpdateDialog by updateViewModel.showDialog.collectAsState()
    
    var currentVideo by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        apiKey = userPreferences.getApiKey()
        allskyUrl = userPreferences.getAllskyUrl()
    }

    LaunchedEffect(apiKey) {
        if (apiKey.isNotEmpty()) {
            weatherViewModel.updateWeather()
        }
    }

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
                onAboutClick = onNavigateToAbout,
                userPreferences = userPreferences,
                languageManager = languageManager,
                updateViewModel = updateViewModel
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
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    if (allskyUrl.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clickable { 
                                    imageViewerViewModel.showImage(liveImageState.imageUrl)
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = liveImageState.imageUrl,
                                    contentDescription = stringResource(R.string.live_allsky_image),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                
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
                    
                    MoonPhaseDisplay()
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
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
                                    text = stringResource(R.string.weather_forecast),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = stringResource(R.string.weather_api_required),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                
                                val uriHandler = LocalUriHandler.current
                                TextButton(
                                    onClick = { 
                                        uriHandler.openUri("https://home.openweathermap.org/api_keys")
                                    }
                                ) {
                                    Text(stringResource(R.string.get_api_key))
                                }
                            }
                        }
                    } else {
                        WeatherDisplay(
                            uiState = weatherUiState,
                            onRequestPermission = onRequestLocationPermission
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    if (allskyUiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = stringResource(R.string.loading_description),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else if (allskyUiState.error != null) {
                        Text(
                            text = allskyUiState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        AllskyMediaSection(
                            title = stringResource(R.string.timelapses),
                            media = allskyUiState.timelapses,
                            onMediaClick = { media -> 
                                currentVideo = media.url
                            },
                            isVideo = true
                        )

                        AllskyMediaSection(
                            title = stringResource(R.string.keograms),
                            media = allskyUiState.keograms,
                            onMediaClick = { media -> 
                                imageViewerViewModel.showImage(media.url)
                            }
                        )

                        AllskyMediaSection(
                            title = stringResource(R.string.startrails),
                            media = allskyUiState.startrails,
                            onMediaClick = { media -> 
                                imageViewerViewModel.showImage(media.url)
                            }
                        )
                    }
                }

                if (imageViewerState.isFullScreen && imageViewerState.currentImageUrl != null) {
                    println("Debug: Showing full screen image: ${imageViewerState.currentImageUrl}")
                    FullScreenImageViewer(
                        imageUrl = imageViewerState.currentImageUrl!!,
                        onDismiss = { imageViewerViewModel.dismissImage() }
                    )
                }

                if (currentVideo != null) {
                    VideoPlayer(
                        videoUrl = currentVideo!!,
                        onDismiss = { currentVideo = null }
                    )
                }
            }
        }
    }

    if (updateState is UpdateUiState.UpdateAvailable && showUpdateDialog) {
        val state = updateState as UpdateUiState.UpdateAvailable
        UpdateDialog(
            showDialog = true,
            onDismiss = {
                updateViewModel.dismissUpdate()
            },
            onDownload = {
                updateViewModel.downloadUpdate()
            },
            version = state.updateInfo.latestVersion,
            changelog = state.updateInfo.releaseNotes
        )
    }
}

private fun formatTime(timestamp: Long): String {
    return if (timestamp == 0L) {
        ""
    } else {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }
}