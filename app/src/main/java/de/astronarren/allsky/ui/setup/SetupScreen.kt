package de.astronarren.allsky.ui.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.astronarren.allsky.viewmodel.SetupViewModel

@Composable
fun SetupScreen(
    viewModel: SetupViewModel,
    onSetupComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isComplete) {
        LaunchedEffect(Unit) {
            onSetupComplete()
        }
        return
    }

    when (uiState.currentStep) {
        1 -> WelcomeStep(onNext = { viewModel.nextStep() })
        2 -> UrlStep(
            currentUrl = uiState.allskyUrl,
            onUrlChange = { viewModel.updateAllskyUrl(it) },
            onNext = { viewModel.nextStep() }
        )
        3 -> ApiKeyStep(
            currentApiKey = uiState.apiKey,
            onApiKeyChange = { viewModel.updateApiKey(it) },
            onComplete = { 
                viewModel.completeSetup()
            }
        )
    }
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Allsky",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Monitor your sky with this Allsky companion app",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Learn more about Allsky",
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.clickable {
                uriHandler.openUri("https://github.com/AllskyTeam/allsky")
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onNext) {
            Text("Start Setup")
        }
    }
}

@Composable
private fun UrlStep(
    currentUrl: String,
    onUrlChange: (String) -> Unit,
    onNext: () -> Unit
) {
    var urlInput by remember { mutableStateOf(currentUrl) }

    LaunchedEffect(urlInput) {
        onUrlChange(urlInput)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Allsky URL",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter the URL of your Allsky installation",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            label = { Text("Allsky URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onNext,
            enabled = urlInput.isNotBlank()
        ) {
            Text("Next")
        }
    }
}

@Composable
private fun ApiKeyStep(
    currentApiKey: String,
    onApiKeyChange: (String) -> Unit,
    onComplete: () -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }

    LaunchedEffect(apiKeyInput) {
        onApiKeyChange(apiKeyInput)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "OpenWeather API Key",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Optional: Add your OpenWeather API key to see weather forecasts",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
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
                    text = "Note: Weather forecasts require GPS permission to detect your current location.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "You can grant this permission later.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            label = { Text("API Key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onComplete) {
            Text("Complete Setup")
        }
    }
} 