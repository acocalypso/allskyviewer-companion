package de.astronarren.allsky.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.astronarren.allsky.BuildConfig
import de.astronarren.allsky.data.UpdateInfo
import de.astronarren.allsky.data.UpdateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UpdateViewModel : ViewModel() {
    private val updateRepository = UpdateRepository()
    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.NoUpdate)
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    init {
        checkForUpdates()
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _uiState.value = UpdateUiState.Checking
            
            val updateInfo = updateRepository.checkForUpdate(BuildConfig.VERSION_NAME)
            _uiState.value = if (updateInfo != null) {
                UpdateUiState.UpdateAvailable(updateInfo)
            } else {
                UpdateUiState.NoUpdate
            }
        }
    }

    fun downloadUpdate(context: Context, updateInfo: UpdateInfo) {
        val request = DownloadManager.Request(Uri.parse(updateInfo.downloadUrl))
            .setTitle("Allsky Update ${updateInfo.latestVersion}")
            .setDescription("Downloading update...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "allsky-${updateInfo.latestVersion}.apk"
            )

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        
        _uiState.value = UpdateUiState.Downloading
    }
}

sealed class UpdateUiState {
    object NoUpdate : UpdateUiState()
    object Checking : UpdateUiState()
    object Downloading : UpdateUiState()
    data class UpdateAvailable(val updateInfo: UpdateInfo) : UpdateUiState()
} 