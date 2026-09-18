package com.laurentvrevin.wheris.feature.template.presentation

import androidx.compose.runtime.Immutable
import com.laurentvrevin.wheris.core.ui.UiText
import com.laurentvrevin.wheris.feature.template.domain.TemplateItem

@Immutable
data class TemplateUiState(
    val items: List<TemplateItem> = emptyList(),
    val isInitialLoading: Boolean = true,
    val error: UiText? = null,
)
