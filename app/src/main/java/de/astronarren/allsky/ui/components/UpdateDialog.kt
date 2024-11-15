package de.astronarren.allsky.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import de.astronarren.allsky.R
import de.astronarren.allsky.data.UpdateInfo

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    onDismiss: () -> Unit,
    onDownload: (UpdateInfo) -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.update_available)) },
        text = {
            Text(
                stringResource(
                    R.string.update_dialog_message,
                    updateInfo.latestVersion,
                    updateInfo.releaseNotes
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { 
                    onDownload(updateInfo)
                    onDismiss()
                }
            ) {
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