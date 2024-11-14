package de.astronarren.allsky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.astronarren.allsky.data.UserPreferences
import de.astronarren.allsky.ui.state.SetupUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SetupViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isComplete = userPreferences.isSetupComplete()
            val url = userPreferences.getAllskyUrl()
            val apiKey = userPreferences.getApiKey()
            
            _uiState.update { state ->
                state.copy(
                    isComplete = isComplete,
                    allskyUrl = url,
                    apiKey = apiKey
                )
            }
        }
    }

    fun nextStep() {
        _uiState.update { state ->
            state.copy(currentStep = state.currentStep + 1)
        }
    }

    fun updateAllskyUrl(url: String) {
        viewModelScope.launch {
            userPreferences.saveAllskyUrl(url)
            _uiState.update { state ->
                state.copy(allskyUrl = url)
            }
        }
    }

    fun updateApiKey(key: String) {
        viewModelScope.launch {
            userPreferences.saveApiKey(key)
            _uiState.update { state ->
                state.copy(apiKey = key)
            }
        }
    }

    fun completeSetup() {
        viewModelScope.launch {
            userPreferences.markSetupComplete()
            _uiState.update { state ->
                state.copy(isComplete = true)
            }
        }
    }
} 