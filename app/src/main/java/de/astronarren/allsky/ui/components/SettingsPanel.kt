package de.astronarren.allsky.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.astronarren.allsky.data.UserPreferences
import de.astronarren.allsky.R
import de.astronarren.allsky.utils.AppLanguage
import de.astronarren.allsky.utils.LanguageManager
import kotlinx.coroutines.launch
import de.astronarren.allsky.BuildConfig
import de.astronarren.allsky.viewmodel.UpdateViewModel
import de.astronarren.allsky.viewmodel.UpdateUiState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.SemanticsProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPanel(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onAboutClick: () -> Unit,
    userPreferences: UserPreferences,
    languageManager: LanguageManager,
    updateViewModel: UpdateViewModel
) {
    var apiKeyInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(languageManager.getCurrentLanguage()) }
    val updateState by updateViewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(isOpen) {
        if (isOpen) {
            apiKeyInput = userPreferences.getApiKey()
            urlInput = userPreferences.getAllskyUrl()
        }
    }

    if (isOpen) {
        val settingsTitle = stringResource(R.string.settings_title)
        val urlInputDescription = stringResource(R.string.allsky_url_input_description)
        val apiKeyInputDescription = stringResource(R.string.api_key_input_description)
        val saveDescription = stringResource(R.string.save_settings_description)
        
        ModalDrawerSheet(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .fillMaxHeight()
                .semantics { 
                    contentDescription = settingsTitle
                },
            drawerContainerColor = MaterialTheme.colorScheme.surface,
            drawerContentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Version info with update badge
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.app_status),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = when (updateState) {
                                    is UpdateUiState.UpdateAvailable -> stringResource(R.string.update_available)
                                    UpdateUiState.Checking -> stringResource(R.string.checking_for_updates)
                                    else -> stringResource(R.string.up_to_date)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (updateState is UpdateUiState.UpdateAvailable) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = stringResource(R.string.update_available_badge),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // URL Input
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text(stringResource(R.string.allsky_url_input)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics(mergeDescendants = true) { 
                            contentDescription = urlInputDescription
                        }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // API Key Input
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = { Text(stringResource(R.string.openweather_api_key)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = apiKeyInputDescription
                        },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Language Selection
                val languageSettings = stringResource(R.string.language_settings)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { showLanguageDialog = true }
                        .semantics {
                            contentDescription = languageSettings
                            role = Role.Button
                        },
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(R.string.open_settings_item),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Save Button
                Button(
                    onClick = {
                        scope.launch {
                            userPreferences.saveAllskyUrl(urlInput)
                            userPreferences.saveApiKey(apiKeyInput)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = saveDescription
                        },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                
                // About Section
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(onClick = onAboutClick),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.about),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.version_number, BuildConfig.VERSION_NAME),
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
                
                Spacer(modifier = Modifier.height(24.dp))
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

private fun isValidUrl(url: String): Boolean =
    url.isEmpty() || url.startsWith("http://") || url.startsWith("https://")