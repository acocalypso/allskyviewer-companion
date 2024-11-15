package de.astronarren.allsky.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.astronarren.allsky.data.UserPreferences
import de.astronarren.allsky.R
import de.astronarren.allsky.utils.AppLanguage
import de.astronarren.allsky.utils.LanguageManager
import kotlinx.coroutines.launch
import de.astronarren.allsky.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPanel(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onAboutClick: () -> Unit,
    userPreferences: UserPreferences,
    languageManager: LanguageManager
) {
    var apiKeyInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(languageManager.getCurrentLanguage()) }
    
    LaunchedEffect(isOpen) {
        if (isOpen) {
            apiKeyInput = userPreferences.getApiKey()
            urlInput = userPreferences.getAllskyUrl()
        }
    }

    if (isOpen) {
        ModalDrawerSheet {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text(stringResource(R.string.allsky_url)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = { Text(stringResource(R.string.openweather_api_key)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Language Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageDialog = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.language_settings),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(currentLanguage.nameResId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = {
                        scope.launch {
                            userPreferences.saveAllskyUrl(urlInput)
                            userPreferences.saveApiKey(apiKeyInput)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.save))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                HorizontalDivider()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAboutClick)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.about),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(
                                R.string.version_number,
                                BuildConfig.VERSION_NAME
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(R.string.open_about_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (showLanguageDialog) {
            LanguageSelector(
                currentLanguage = currentLanguage,
                onLanguageSelected = { language ->
                    currentLanguage = language
                    languageManager.setLanguage(language)
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
} 