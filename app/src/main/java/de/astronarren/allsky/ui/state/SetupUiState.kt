package de.astronarren.allsky.ui.state

data class SetupUiState(
    val currentStep: Int = 1,
    val allskyUrl: String = "",
    val apiKey: String = "",
    val isComplete: Boolean = false,
    val error: String? = null
) 