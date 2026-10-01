package com.laurentvrevin.wheris.feature.pindetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.core.model.PinId
import com.laurentvrevin.wheris.domain.PinRepository
import com.laurentvrevin.wheris.domain.usecase.UpdatePinDetailsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface EditPinUiState {
    data object Loading : EditPinUiState

    data object NotFound : EditPinUiState

    data object LoadFailed : EditPinUiState

    data object Saved : EditPinUiState

    data class Content(
        val pinId: PinId,
        val name: String,
        val note: String,
        val isSaving: Boolean = false,
        val saveFailed: Boolean = false,
        val noLongerExists: Boolean = false,
    ) : EditPinUiState
}

class EditPinViewModel(
    private val pinRepository: PinRepository,
    private val updateDetails: UpdatePinDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow<EditPinUiState>(EditPinUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var loadedId: PinId? = null
    private var loading = false

    fun load(pinId: PinId) {
        if (loading || (loadedId == pinId && _uiState.value != EditPinUiState.LoadFailed)) return
        loading = true
        loadedId = pinId
        _uiState.value = EditPinUiState.Loading
        viewModelScope.launch {
            try {
                val pin = pinRepository.observePin(pinId).first()
                _uiState.value =
                    if (pin == null) {
                        EditPinUiState.NotFound
                    } else {
                        val restoreDraft = savedStateHandle.get<String>(DRAFT_ID) == pinId.value
                        val name = if (restoreDraft) savedStateHandle.get<String>(DRAFT_NAME).orEmpty() else pin.name.orEmpty()
                        val note = if (restoreDraft) savedStateHandle.get<String>(DRAFT_NOTE).orEmpty() else pin.note.orEmpty()
                        savedStateHandle[DRAFT_ID] = pinId.value
                        savedStateHandle[DRAFT_NAME] = name
                        savedStateHandle[DRAFT_NOTE] = note
                        EditPinUiState.Content(pinId, name, note)
                    }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.value = EditPinUiState.LoadFailed
            } finally {
                loading = false
            }
        }
    }

    fun changeName(name: String) {
        val current = _uiState.value as? EditPinUiState.Content ?: return
        if (current.isSaving) return
        savedStateHandle[DRAFT_NAME] = name
        _uiState.value = current.copy(name = name, saveFailed = false)
    }

    fun changeNote(note: String) {
        val current = _uiState.value as? EditPinUiState.Content ?: return
        if (current.isSaving) return
        savedStateHandle[DRAFT_NOTE] = note
        _uiState.value = current.copy(note = note, saveFailed = false)
    }

    fun save() {
        val current = _uiState.value as? EditPinUiState.Content ?: return
        if (current.isSaving || current.noLongerExists) return
        _uiState.value = current.copy(isSaving = true, saveFailed = false)
        viewModelScope.launch {
            try {
                if (updateDetails(current.pinId, current.name, current.note)) {
                    savedStateHandle.remove<String>(DRAFT_ID)
                    savedStateHandle.remove<String>(DRAFT_NAME)
                    savedStateHandle.remove<String>(DRAFT_NOTE)
                    _uiState.value = EditPinUiState.Saved
                } else {
                    _uiState.value = current.copy(noLongerExists = true)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.value = current.copy(saveFailed = true)
            }
        }
    }

    private companion object {
        const val DRAFT_ID = "editPin.id"
        const val DRAFT_NAME = "editPin.name"
        const val DRAFT_NOTE = "editPin.note"
    }
}
