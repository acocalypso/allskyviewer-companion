package de.astronarren.allsky.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Allsky Companion App",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Version 1.0",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "© 2024 astro-narren.de / AcoVanConis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle(text = "Weather Data")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "This app uses the OpenWeather API for weather forecasts. You need an API key to access weather data.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    val uriHandler = LocalUriHandler.current
                    Button(
                        onClick = { uriHandler.openUri("https://openweathermap.org/") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    ) {
                        Text("Visit OpenWeather")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle(text = "Open Source Libraries")
            
            LibraryInfo(
                name = "Jetpack Compose",
                description = "Modern toolkit for building native Android UI",
                license = "Apache License 2.0",
                url = "https://developer.android.com/jetpack/compose"
            )
            
            LibraryInfo(
                name = "Retrofit",
                description = "Type-safe HTTP client for Android and Java",
                license = "Apache License 2.0",
                url = "https://square.github.io/retrofit/"
            )
            
            LibraryInfo(
                name = "Coil",
                description = "Image loading for Android",
                license = "Apache License 2.0",
                url = "https://coil-kt.github.io/coil/"
            )
            
            LibraryInfo(
                name = "Jsoup",
                description = "Java HTML Parser",
                license = "MIT License",
                url = "https://jsoup.org/"
            )
            
            LibraryInfo(
                name = "Kotlin Coroutines",
                description = "Asynchronous programming for Kotlin",
                license = "Apache License 2.0",
                url = "https://github.com/Kotlin/kotlinx.coroutines"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun LibraryInfo(
    name: String,
    description: String,
    license: String,
    url: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Text(
                text = "License: $license",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            val uriHandler = LocalUriHandler.current
            TextButton(
                onClick = { uriHandler.openUri(url) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Learn More")
            }
        }
    }
} 