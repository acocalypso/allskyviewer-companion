package de.astronarren.allsky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.astronarren.allsky.data.UserPreferences
import de.astronarren.allsky.ui.state.LiveImageUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class LiveImageViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(LiveImageUiState())
    val uiState: StateFlow<LiveImageUiState> = _uiState.asStateFlow()

    init {
        startImageRefresh()
        observeUrlChanges()
    }

    private fun startImageRefresh() {
        viewModelScope.launch {
            while (true) {
                updateImage()
                delay(30_000) // 30 seconds
            }
        }
    }

    private fun observeUrlChanges() {
        viewModelScope.launch {
            userPreferences.getAllskyUrlFlow().collect { url ->
                updateImage(url)
            }
        }
    }

    private suspend fun updateImage(baseUrl: String? = null) {
        val url = baseUrl ?: userPreferences.getAllskyUrl()
        if (url.isNotEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    imageUrl = "$url/image.jpg?t=${System.currentTimeMillis()}",
                    lastUpdate = System.currentTimeMillis()
                )
            }
        }
    }
} 