package de.astronarren.allsky.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.astronarren.allsky.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.clickable

// Update data class to not use stringResource directly
private data class ComponentInfo(
    val nameResId: Int,
    val url: String,
    val licenseResId: Int = R.string.apache_license
)

@Composable
private fun ComponentLink(
    component: ComponentInfo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = null,
            modifier = Modifier.size(8.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "${stringResource(component.nameResId)} (${stringResource(component.licenseResId)})",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Title and Version
            Text(
                text = stringResource(R.string.about_title),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = stringResource(R.string.about_version, "1.0"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Copyright Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.about_copyright),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.about_description),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Features Section
            Text(
                text = stringResource(R.string.about_features),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Feature Cards
            FeatureCard(
                icon = Icons.Outlined.Refresh,
                title = stringResource(R.string.feature_live_view_title),
                description = stringResource(R.string.feature_live_view_desc)
            )
            
            FeatureCard(
                icon = Icons.Outlined.NightsStay,
                title = stringResource(R.string.feature_moon_phase_title),
                description = stringResource(R.string.feature_moon_phase_desc)
            )
            
            FeatureCard(
                icon = Icons.Outlined.WbSunny,
                title = stringResource(R.string.feature_weather_title),
                description = stringResource(R.string.feature_weather_desc)
            )
            
            FeatureCard(
                icon = Icons.Outlined.Collections,
                title = stringResource(R.string.feature_media_title),
                description = stringResource(R.string.feature_media_desc)
            )
            
            FeatureCard(
                icon = Icons.Outlined.Palette,
                title = stringResource(R.string.feature_theme_title),
                description = stringResource(R.string.feature_theme_desc)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Components Section
            Text(
                text = stringResource(R.string.about_components),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Components Cards
            ComponentCard(
                title = stringResource(R.string.component_section_ui),
                components = listOf(
                    ComponentInfo(
                        R.string.component_jetpack_compose,
                        "https://developer.android.com/jetpack/compose"
                    ),
                    ComponentInfo(
                        R.string.component_material_design,
                        "https://m3.material.io/"
                    )
                )
            )
            
            ComponentCard(
                title = stringResource(R.string.component_section_core),
                components = listOf(
                    ComponentInfo(
                        R.string.component_coroutines,
                        "https://github.com/Kotlin/kotlinx.coroutines"
                    ),
                    ComponentInfo(
                        R.string.component_retrofit,
                        "https://github.com/square/retrofit"
                    ),
                    ComponentInfo(
                        R.string.component_coil,
                        "https://github.com/coil-kt/coil"
                    )
                )
            )
            
            ComponentCard(
                title = stringResource(R.string.component_section_android),
                components = listOf(
                    ComponentInfo(
                        R.string.component_datastore,
                        "https://developer.android.com/topic/libraries/architecture/datastore"
                    ),
                    ComponentInfo(
                        R.string.component_play_services,
                        "https://developers.google.com/android/guides/setup"
                    ),
                    ComponentInfo(
                        R.string.component_navigation,
                        "https://developer.android.com/guide/navigation"
                    ),
                    ComponentInfo(
                        R.string.component_lifecycle,
                        "https://developer.android.com/topic/libraries/architecture/lifecycle"
                    )
                )
            )
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ComponentCard(
    title: String,
    components: List<ComponentInfo>
) {
    val uriHandler = LocalUriHandler.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            components.forEach { component ->
                ComponentLink(
                    component = component,
                    onClick = { uriHandler.openUri(component.url) }
                )
            }
        }
    }
} 