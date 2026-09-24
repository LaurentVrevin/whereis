package com.laurentvrevin.wheris.feature.home

import com.laurentvrevin.wheris.core.model.Pin

data class HomeUiState(
    val pins: List<Pin> = emptyList(),
    val dataUnavailable: Boolean = false,
)
