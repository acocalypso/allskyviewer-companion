package de.astronarren.allsky.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.astronarren.allsky.R
import de.astronarren.allsky.utils.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = AppLanguage.values()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.language_settings),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            
            languages.forEach { language ->
                ListItem(
                    headlineContent = { 
                        Text(stringResource(language.nameResId))
                    },
                    leadingContent = {
                        if (language == AppLanguage.SYSTEM) {
                            Icon(
                                imageVector = Icons.Outlined.PhoneAndroid,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = language.code.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    trailingContent = {
                        if (language == currentLanguage) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.clickable {
                        onLanguageSelected(language)
                        onDismiss()
                    }
                )
            }
        }
    }
} 