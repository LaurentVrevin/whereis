package com.laurentvrevin.androidstarter.designsystem.ui

import androidx.compose.runtime.Immutable

/**
 * Interface optionnelle pour représenter l'état d'un écran.
 * Privilégier des data classes spécifiques par feature comme TemplateUiState.
 */
@Immutable
sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<out T>(val data: T) : UiState<T>

    data class Error(val message: String) : UiState<Nothing>
}
