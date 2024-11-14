package de.astronarren.allsky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.astronarren.allsky.data.AllskyRepository
import de.astronarren.allsky.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AllskyUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val timelapses: List<AllskyMediaUiState> = emptyList(),
    val keograms: List<AllskyMediaUiState> = emptyList(),
    val startrails: List<AllskyMediaUiState> = emptyList()
)

data class AllskyMediaUiState(
    val date: String,
    val url: String
)

class AllskyViewModel(
    private val allskyRepository: AllskyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(AllskyUiState(isLoading = true))
    val uiState: StateFlow<AllskyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Observe URL changes
            userPreferences.getAllskyUrlFlow().collect { _ ->
                loadContent()
            }
        }
    }

    private fun loadContent() {
        viewModelScope.launch {
            _uiState.update { currentState -> 
                currentState.copy(isLoading = true, error = null) 
            }
            try {
                val content = allskyRepository.getAllContent()
                _uiState.value = AllskyUiState(
                    isLoading = false,
                    timelapses = content.timelapses.map { 
                        AllskyMediaUiState(it.date, it.url) 
                    },
                    keograms = content.keograms.map { 
                        AllskyMediaUiState(it.date, it.url) 
                    },
                    startrails = content.startrails.map { 
                        AllskyMediaUiState(it.date, it.url) 
                    }
                )
            } catch (e: Exception) {
                _uiState.value = AllskyUiState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
} 