package de.astronarren.allsky.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import de.astronarren.allsky.R
import de.astronarren.allsky.data.UpdateInfo

@Composable
fun UpdateDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    version: String,
    changelog: String
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.update_available)) },
            text = {
                Text(
                    stringResource(
                        R.string.update_dialog_message,
                        version,
                        changelog
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = onDownload) {
                    Text(stringResource(R.string.download_update))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.later))
                }
            }
        )
    }
} 