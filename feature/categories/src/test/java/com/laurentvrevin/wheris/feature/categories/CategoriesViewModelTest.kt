package com.laurentvrevin.wheris.feature.categories

import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.domain.repository.CategoryMutationResult
import com.laurentvrevin.wheris.domain.repository.CategoryRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeCategories()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun loaded(): CategoriesViewModel = CategoriesViewModel(repository).also { dispatcher.scheduler.advanceUntilIdle() }

    private fun editor(vm: CategoriesViewModel) = vm.uiState.value.surface as CategorySurface.Editor

    private fun deletion(vm: CategoriesViewModel) = vm.uiState.value.surface as CategorySurface.Delete

    @Test
    fun observationFailureDuringEditingKeepsDraftAndRealErrorUntilReload() =
        runTest {
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            vm.updateName("Local draft")
            val draft = editor(vm)
            repository.failObservation = true
            vm.retryObservation()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(vm.uiState.value.loadFailed)
            assertEquals(draft, editor(vm))
            repository.result = CategoryMutationResult.TECHNICAL_FAILURE
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(vm.uiState.value.loadFailed)
            assertEquals("Local draft", editor(vm).name)
            repository.failObservation = false
            vm.retryObservation()
            dispatcher.scheduler.advanceUntilIdle()
            assertFalse(vm.uiState.value.loadFailed)
            assertEquals("Local draft", editor(vm).name)
        }

    @Test
    fun loadingBecomesListInRepositoryOrderAndObservationCanBeRetried() =
        runTest {
            val vm = CategoriesViewModel(repository)
            assertTrue(vm.uiState.value.isLoading)
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(repository.rows.value, vm.uiState.value.categories)
            assertFalse(vm.uiState.value.isLoading)
            repository.failObservation = true
            vm.retryObservation()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(vm.uiState.value.loadFailed)
            assertEquals(repository.rows.value, vm.uiState.value.categories)
            vm.openCreate()
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
            repository.failObservation = false
            vm.retryObservation()
            dispatcher.scheduler.advanceUntilIdle()
            assertFalse(vm.uiState.value.loadFailed)
        }

    @Test
    fun creationValidatesTrimsAndPersistsIconAndColorWithoutPlaceSelection() =
        runTest {
            val vm = loaded()
            vm.openCreate()
            vm.updateName(" \t\n")
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(0, repository.createCalls)
            vm.updateName("  茸 — Champignons  ")
            vm.selectIcon(CategoryIconKey.PHOTO_CAMERA)
            vm.selectColor(CategoryColorKey.TEAL)
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
            assertEquals("茸 — Champignons", repository.created?.name)
            assertEquals(CategoryIconKey.PHOTO_CAMERA, repository.created?.iconKey)
            assertEquals(CategoryColorKey.TEAL, repository.created?.colorKey)
            assertEquals(1, repository.createCalls)
        }

    @Test
    fun creationFailureKeepsAllChoicesAndRetrySucceeds() =
        runTest {
            val vm = loaded()
            vm.openCreate()
            vm.updateName("Draft")
            vm.selectIcon(CategoryIconKey.PARK)
            vm.selectColor(CategoryColorKey.PURPLE)
            val draft = editor(vm)
            repository.failCreate = true
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(draft.copy(error = CategoryMessage.SAVE_FAILED), editor(vm))
            repository.failCreate = false
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
        }

    @Test
    fun pendingCreationBlocksDoubleSubmitEditsAndBack() =
        runTest {
            val vm = loaded()
            vm.openCreate()
            vm.updateName("Draft")
            repository.pause = CompletableDeferred()
            vm.save()
            vm.save()
            vm.back()
            vm.updateName("Ignored")
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(editor(vm).isBusy)
            assertEquals("Draft", editor(vm).name)
            assertEquals(1, repository.createCalls)
            repository.pause?.complete(Unit)
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
        }

    @Test
    fun cancelCreateAndEditNeverWritesAndSystemCannotOpenEditor() =
        runTest {
            val vm = loaded()
            vm.openEdit(SystemCategoryIds.OTHER)
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
            vm.openCreate()
            vm.updateName("Cancelled")
            vm.back()
            vm.openEdit(repository.custom.id)
            assertEquals(repository.custom.name, editor(vm).name)
            assertEquals(repository.custom.iconKey, editor(vm).iconKey)
            assertEquals(repository.custom.colorKey, editor(vm).colorKey)
            vm.updateName("Unsaved")
            vm.back()
            assertEquals(0, repository.createCalls)
            assertEquals(0, repository.updateCalls)
            assertEquals(repository.custom, repository.rows.value.last())
        }

    @Test
    fun editDraftSurvivesFlowAndSuccessfulSavePreservesIdAndCreationTime() =
        runTest {
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            vm.updateName("  Local draft  ")
            vm.selectIcon(CategoryIconKey.RESTAURANT)
            vm.selectColor(CategoryColorKey.BLUE)
            val draft = editor(vm)
            repository.rows.value = repository.rows.value.map { if (it.id == repository.custom.id) it.copy(name = "External") else it }
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(draft, editor(vm))
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            val updated = repository.rows.value.last()
            assertEquals(repository.custom.id, updated.id)
            assertEquals(repository.custom.createdAtEpochMillis, updated.createdAtEpochMillis)
            assertEquals("Local draft", updated.name)
            assertEquals(CategoryIconKey.RESTAURANT, updated.iconKey)
            assertEquals(CategoryColorKey.BLUE, updated.colorKey)
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
        }

    @Test
    fun editErrorPreservesDraftAndRetrySucceeds() =
        runTest {
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            vm.updateName("Draft")
            repository.result = CategoryMutationResult.TECHNICAL_FAILURE
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals("Draft", editor(vm).name)
            assertEquals(CategoryMessage.SAVE_FAILED, editor(vm).error)
            repository.result = CategoryMutationResult.SUCCESS
            vm.save()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
        }

    @Test
    fun disappearedSourceReturnsToListAndCountFailureKeepsEditorForRetry() =
        runTest {
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            repository.failCount = true
            vm.requestDelete()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CategoryMessage.USAGE_FAILED, editor(vm).error)
            repository.failCount = false
            vm.requestDelete()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(0, deletion(vm).usageCount)
            repository.rows.value = repository.rows.value.filterNot { it.id == repository.custom.id }
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
            assertEquals(CategoryMessage.NOT_FOUND, vm.uiState.value.notice)
        }

    @Test
    fun unusedConfirmationCanBeCancelledAndDeletionFailureRetried() =
        runTest {
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            vm.updateName("Unsaved draft")
            val draft = editor(vm)
            vm.requestDelete()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(0, deletion(vm).usageCount)
            vm.back()
            assertEquals(draft, editor(vm))
            assertEquals(0, repository.deleteCalls)
            vm.requestDelete()
            dispatcher.scheduler.advanceUntilIdle()
            repository.result = CategoryMutationResult.TECHNICAL_FAILURE
            vm.confirmDelete()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CategoryMessage.DELETE_FAILED, deletion(vm).error)
            repository.result = CategoryMutationResult.SUCCESS
            vm.confirmDelete()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
            assertTrue(vm.uiState.value.categories.none { it.id == repository.custom.id })
        }

    @Test
    fun unusedInUseRaceReloadsCountAndSwitchesToExplicitReplacement() =
        runTest {
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            vm.requestDelete()
            dispatcher.scheduler.advanceUntilIdle()
            repository.result = CategoryMutationResult.IN_USE
            repository.count = 2
            vm.confirmDelete()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(2, deletion(vm).usageCount)
            assertEquals(null, deletion(vm).replacementId)
            assertEquals(null, deletion(vm).error)
            assertEquals(2, repository.countCalls)
        }

    @Test
    fun usedCategoryThroughOtherConfirmationReturnsToListAndPreservesPlaces() =
        runTest {
            repository.count = 3
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            vm.requestDelete()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(3, deletion(vm).usageCount)
            vm.confirmDelete()
            assertEquals(0, repository.reassignCalls)
            vm.selectReplacement(repository.custom.id)
            assertEquals(null, deletion(vm).replacementId)
            vm.selectReplacement(SystemCategoryIds.OTHER)
            vm.confirmDelete()
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(repository.custom.id to SystemCategoryIds.OTHER, repository.reassignment)
            assertEquals(3, repository.places.size)
            assertTrue(repository.places.all { it == SystemCategoryIds.OTHER })
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
            assertTrue(vm.uiState.value.categories.any { it.id == SystemCategoryIds.OTHER })
        }

    @Test
    fun customTargetIsSelectableButItsDisappearanceClearsChoiceAndKeepsDraft() =
        runTest {
            repository.count = 1
            val target = repository.custom.copy(id = CategoryId("target"), name = "Target")
            repository.rows.value += target
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            vm.updateName("Local draft")
            vm.requestDelete()
            dispatcher.scheduler.advanceUntilIdle()
            vm.selectReplacement(target.id)
            assertEquals(target.id, deletion(vm).replacementId)
            repository.rows.value = repository.rows.value.filterNot { it.id == target.id }
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(null, deletion(vm).replacementId)
            assertEquals(CategoryMessage.REPLACEMENT_NOT_FOUND, deletion(vm).error)
            vm.back()
            assertEquals("Local draft", editor(vm).name)
            assertEquals(0, repository.reassignCalls)
        }

    @Test
    fun replacementMissingSameCategoryAndTechnicalResultsKeepDeletionContext() =
        runTest {
            repository.count = 2
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            vm.requestDelete()
            dispatcher.scheduler.advanceUntilIdle()
            listOf(
                CategoryMutationResult.REPLACEMENT_NOT_FOUND to CategoryMessage.REPLACEMENT_NOT_FOUND,
                CategoryMutationResult.SAME_CATEGORY to CategoryMessage.INVALID_REPLACEMENT,
                CategoryMutationResult.TECHNICAL_FAILURE to CategoryMessage.DELETE_FAILED,
            ).forEach { (result, message) ->
                vm.selectReplacement(SystemCategoryIds.OTHER)
                repository.result = result
                vm.confirmDelete()
                dispatcher.scheduler.advanceUntilIdle()
                assertEquals(message, deletion(vm).error)
                assertFalse(deletion(vm).isBusy)
                assertEquals(repository.custom.id, deletion(vm).editor.categoryId)
            }
        }

    @Test
    fun notFoundAndProtectedResultsReturnSafelyToList() =
        runTest {
            val vm = loaded()
            listOf(
                CategoryMutationResult.NOT_FOUND to CategoryMessage.NOT_FOUND,
                CategoryMutationResult.SYSTEM_PROTECTED to CategoryMessage.SYSTEM_PROTECTED,
            ).forEach { (result, message) ->
                vm.openEdit(repository.custom.id)
                repository.result = result
                vm.save()
                dispatcher.scheduler.advanceUntilIdle()
                assertEquals(CategorySurface.List, vm.uiState.value.surface)
                assertEquals(message, vm.uiState.value.notice)
            }
        }

    @Test
    fun pendingDeletionBlocksDoubleConfirmationReplacementChangesAndBack() =
        runTest {
            repository.count = 2
            val vm = loaded()
            vm.openEdit(repository.custom.id)
            vm.requestDelete()
            dispatcher.scheduler.advanceUntilIdle()
            vm.selectReplacement(SystemCategoryIds.OTHER)
            repository.pause = CompletableDeferred()
            vm.confirmDelete()
            vm.confirmDelete()
            vm.back()
            vm.selectReplacement(SystemCategoryIds.CAR)
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(deletion(vm).isBusy)
            assertEquals(SystemCategoryIds.OTHER, deletion(vm).replacementId)
            assertEquals(1, repository.reassignCalls)
            repository.pause?.complete(Unit)
            dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CategorySurface.List, vm.uiState.value.surface)
        }

    private class FakeCategories : CategoryRepository {
        val custom = Category(CategoryId("custom"), false, "Custom", CategoryIconKey.PARK, CategoryColorKey.PURPLE, 1000L)
        val rows = MutableStateFlow(listOf(Category(SystemCategoryIds.CAR, true), Category(SystemCategoryIds.OTHER, true), custom))
        var failObservation = false
        var failCreate = false
        var failCount = false
        var count = 0
        var places = List(3) { custom.id }
        var countCalls = 0
        var createCalls = 0
        var updateCalls = 0
        var deleteCalls = 0
        var reassignCalls = 0
        var created: Category? = null
        var reassignment: Pair<CategoryId, CategoryId>? = null
        var result = CategoryMutationResult.SUCCESS
        var pause: CompletableDeferred<Unit>? = null

        override fun observeCategories(): Flow<List<Category>> = if (failObservation) flow { error("Synthetic failure") } else rows

        override fun observeSystemCategories(): Flow<List<Category>> = error("Must observe all")

        override suspend fun createCustomCategory(
            name: String,
            iconKey: CategoryIconKey,
            colorKey: CategoryColorKey,
        ): Category {
            createCalls++
            pause?.await()
            if (failCreate) error("Synthetic failure")
            return Category(CategoryId("new-$createCalls"), false, name, iconKey, colorKey, 2000L).also {
                created = it
                rows.value += it
            }
        }

        override suspend fun updateCustomCategory(
            id: CategoryId,
            name: String,
            iconKey: CategoryIconKey,
            colorKey: CategoryColorKey,
        ): CategoryMutationResult {
            updateCalls++
            pause?.await()
            if (result == CategoryMutationResult.SUCCESS) {
                rows.value =
                    rows.value.map {
                        if (it.id == id) it.copy(name = name, iconKey = iconKey, colorKey = colorKey) else it
                    }
            }
            return result
        }

        override suspend fun getCategoryUsageCount(categoryId: CategoryId): Int {
            countCalls++
            if (failCount) error("Synthetic failure")
            return count
        }

        override suspend fun deleteCustomCategory(categoryId: CategoryId): CategoryMutationResult {
            deleteCalls++
            pause?.await()
            if (result == CategoryMutationResult.SUCCESS) rows.value = rows.value.filterNot { it.id == categoryId }
            return result
        }

        override suspend fun reassignAndDeleteCustomCategory(
            sourceCategoryId: CategoryId,
            replacementCategoryId: CategoryId,
        ): CategoryMutationResult {
            reassignCalls++
            pause?.await()
            if (result == CategoryMutationResult.SUCCESS) {
                reassignment = sourceCategoryId to replacementCategoryId
                places = places.map { if (it == sourceCategoryId) replacementCategoryId else it }
                rows.value = rows.value.filterNot { it.id == sourceCategoryId }
            }
            return result
        }
    }
}
