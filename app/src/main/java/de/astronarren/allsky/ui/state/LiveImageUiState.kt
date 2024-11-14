package de.astronarren.allsky.ui.state

data class LiveImageUiState(
    val imageUrl: String = "",
    val lastUpdate: Long = 0L,
    val error: String? = null
) 