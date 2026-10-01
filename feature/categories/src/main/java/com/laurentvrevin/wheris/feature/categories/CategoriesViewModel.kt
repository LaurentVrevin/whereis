package com.laurentvrevin.wheris.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.domain.repository.CategoryMutationResult
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: CategoryRepository) : ViewModel() {
    private val state = MutableStateFlow(CategoriesUiState())
    val uiState = state.asStateFlow()
    private var observation: Job? = null

    init {
        observeCategories()
    }

    fun retryObservation() = observeCategories()

    private fun observeCategories() {
        observation?.cancel()
        state.value = state.value.copy(isLoading = true, loadFailed = false)
        observation =
            viewModelScope.launch {
                try {
                    repository.observeCategories().collect { categories -> acceptCategories(categories) }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    state.value = state.value.copy(isLoading = false, loadFailed = true)
                }
            }
    }

    private fun acceptCategories(categories: List<Category>) {
        state.value = normalizeSurface(state.value.copy(categories = categories, isLoading = false, loadFailed = false))
    }

    private fun normalizeSurface(current: CategoriesUiState): CategoriesUiState {
        var next = current
        val categories = next.categories
        val surface = next.surface
        val sourceId =
            when (surface) {
                is CategorySurface.Editor -> surface.categoryId.takeUnless { surface.isBusy }
                is CategorySurface.Delete -> surface.editor.categoryId.takeUnless { surface.isBusy }
                CategorySurface.List -> null
            }
        if (sourceId != null && categories.none { it.id == sourceId && !it.isSystem }) {
            next = next.copy(surface = CategorySurface.List, notice = CategoryMessage.NOT_FOUND)
        } else if (surface is CategorySurface.Delete && !surface.isBusy && surface.replacementId != null &&
            categories.none { it.id == surface.replacementId && it.id != sourceId }
        ) {
            next = next.copy(surface = surface.copy(replacementId = null, error = CategoryMessage.REPLACEMENT_NOT_FOUND))
        }
        // Only the catalog changes: a live editor's local draft is never overwritten.
        return next
    }

    fun openCreate() {
        if (state.value.surface != CategorySurface.List || state.value.isLoading || state.value.loadFailed) return
        state.value = state.value.copy(surface = CategorySurface.Editor(), notice = null)
    }

    fun openEdit(id: CategoryId) {
        if (state.value.surface != CategorySurface.List) return
        val category = state.value.categories.firstOrNull { it.id == id && !it.isSystem } ?: return
        state.value =
            state.value.copy(
                surface =
                    CategorySurface.Editor(
                        category.id, requireNotNull(category.name), requireNotNull(category.iconKey),
                        requireNotNull(category.colorKey),
                    ),
                notice = null,
            )
    }

    fun updateName(name: String) = updateEditor { it.copy(name = name, error = null) }

    fun selectIcon(key: CategoryIconKey) = updateEditor { it.copy(iconKey = key, error = null) }

    fun selectColor(key: CategoryColorKey) = updateEditor { it.copy(colorKey = key, error = null) }

    private fun updateEditor(transform: (CategorySurface.Editor) -> CategorySurface.Editor) {
        val editor = state.value.surface as? CategorySurface.Editor ?: return
        if (!editor.isBusy) state.value = state.value.copy(surface = transform(editor))
    }

    fun back() {
        when (val surface = state.value.surface) {
            is CategorySurface.Editor -> if (!surface.isBusy) state.value = state.value.copy(surface = CategorySurface.List)
            is CategorySurface.Delete -> if (!surface.isBusy) state.value = state.value.copy(surface = surface.editor)
            CategorySurface.List -> Unit
        }
    }

    fun save() {
        val editor = state.value.surface as? CategorySurface.Editor ?: return
        if (!editor.canSave) return
        state.value = state.value.copy(surface = editor.copy(isBusy = true, error = null))
        viewModelScope.launch {
            try {
                if (editor.categoryId == null) {
                    repository.createCustomCategory(editor.name.trim(), editor.iconKey, editor.colorKey)
                    showList()
                } else {
                    finishMutation(repository.updateCustomCategory(editor.categoryId, editor.name.trim(), editor.iconKey, editor.colorKey))
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                setFailure(CategoryMessage.SAVE_FAILED)
            }
        }
    }

    fun requestDelete() {
        val editor = state.value.surface as? CategorySurface.Editor ?: return
        if (editor.isBusy || editor.categoryId == null) return
        loadUsage(editor)
    }

    private fun loadUsage(editor: CategorySurface.Editor) {
        val id = editor.categoryId ?: return
        state.value = state.value.copy(surface = editor.copy(isBusy = true, error = null))
        viewModelScope.launch {
            try {
                val count = repository.getCategoryUsageCount(id)
                require(count >= 0)
                if (state.value.categories.none { it.id == id && !it.isSystem }) {
                    showList(CategoryMessage.NOT_FOUND)
                } else {
                    state.value = state.value.copy(surface = CategorySurface.Delete(editor.copy(isBusy = false, error = null), count))
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                setFailure(CategoryMessage.USAGE_FAILED)
            }
        }
    }

    fun selectReplacement(id: CategoryId) {
        val deletion = state.value.surface as? CategorySurface.Delete ?: return
        if (deletion.isBusy || deletion.usageCount == 0 || id == deletion.editor.categoryId) return
        if (state.value.categories.none { it.id == id }) return
        state.value = state.value.copy(surface = deletion.copy(replacementId = id, error = null))
    }

    fun confirmDelete() {
        val deletion = state.value.surface as? CategorySurface.Delete ?: return
        if (deletion.isBusy) return
        val source = deletion.editor.categoryId ?: return
        if (deletion.usageCount > 0 && (deletion.replacementId == null || state.value.isLoading || state.value.loadFailed)) return
        state.value = state.value.copy(surface = deletion.copy(isBusy = true, error = null))
        viewModelScope.launch {
            try {
                val result =
                    if (deletion.usageCount == 0) {
                        repository.deleteCustomCategory(source)
                    } else {
                        repository.reassignAndDeleteCustomCategory(source, requireNotNull(deletion.replacementId))
                    }
                finishMutation(result)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                setFailure(CategoryMessage.DELETE_FAILED)
            }
        }
    }

    private fun finishMutation(result: CategoryMutationResult) {
        when (result) {
            CategoryMutationResult.SUCCESS -> showList()
            CategoryMutationResult.NOT_FOUND -> showList(CategoryMessage.NOT_FOUND)
            CategoryMutationResult.SYSTEM_PROTECTED -> showList(CategoryMessage.SYSTEM_PROTECTED)
            CategoryMutationResult.IN_USE -> {
                when (val surface = state.value.surface) {
                    is CategorySurface.Delete -> loadUsage(surface.editor)
                    is CategorySurface.Editor -> loadUsage(surface.copy(isBusy = false))
                    CategorySurface.List -> Unit
                }
            }
            CategoryMutationResult.REPLACEMENT_NOT_FOUND -> {
                clearReplacement(CategoryMessage.REPLACEMENT_NOT_FOUND)
                observeCategories()
            }
            CategoryMutationResult.SAME_CATEGORY -> clearReplacement(CategoryMessage.INVALID_REPLACEMENT)
            CategoryMutationResult.TECHNICAL_FAILURE ->
                setFailure(
                    if (state.value.surface is CategorySurface.Delete) CategoryMessage.DELETE_FAILED else CategoryMessage.SAVE_FAILED,
                )
        }
    }

    private fun clearReplacement(message: CategoryMessage) {
        val deletion = state.value.surface as? CategorySurface.Delete
        if (deletion != null) {
            state.value = state.value.copy(surface = deletion.copy(isBusy = false, replacementId = null, error = message))
        } else {
            setFailure(message)
        }
    }

    private fun setFailure(message: CategoryMessage) {
        val surface =
            when (val current = state.value.surface) {
                is CategorySurface.Editor -> current.copy(isBusy = false, error = message)
                is CategorySurface.Delete -> current.copy(isBusy = false, error = message)
                CategorySurface.List -> current
            }
        state.value = normalizeSurface(state.value.copy(surface = surface))
    }

    private fun showList(notice: CategoryMessage? = null) {
        state.value = state.value.copy(surface = CategorySurface.List, notice = notice)
        observeCategories()
    }
}
