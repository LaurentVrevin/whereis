package com.laurentvrevin.androidstarter.feature.template.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.androidstarter.core.ui.UiText
import com.laurentvrevin.androidstarter.feature.template.domain.TemplateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TemplateViewModel(
    private val repository: TemplateRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TemplateUiState())
    val state: StateFlow<TemplateUiState> = _state.asStateFlow()

    init {
        observeTemplates()
    }

    private fun observeTemplates() {
        repository.getTemplates()
            .onEach { items ->
                _state.update { it.copy(items = items, isInitialLoading = false) }
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        isInitialLoading = false,
                        error = UiText.DynamicString(e.message ?: "Unknown Error"),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun addTemplate(
        title: String,
        description: String,
    ) {
        viewModelScope.launch {
            repository.addTemplate(title, description)
        }
    }

    fun deleteTemplate(id: Int) {
        viewModelScope.launch {
            repository.deleteTemplate(id)
        }
    }
}
