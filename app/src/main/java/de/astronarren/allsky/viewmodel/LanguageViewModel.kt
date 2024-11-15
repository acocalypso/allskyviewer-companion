package de.astronarren.allsky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.astronarren.allsky.utils.LanguageManager
import de.astronarren.allsky.utils.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LanguageViewModel(
    private val languageManager: LanguageManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(languageManager.getCurrentLanguage())
    val uiState: StateFlow<AppLanguage> = _uiState.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            languageManager.setLanguage(language)
            _uiState.value = language
        }
    }
} 