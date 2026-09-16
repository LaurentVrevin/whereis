package com.laurentvrevin.androidstarter.feature.template

import com.laurentvrevin.androidstarter.feature.template.domain.TemplateItem
import com.laurentvrevin.androidstarter.feature.template.domain.TemplateRepository
import com.laurentvrevin.androidstarter.feature.template.presentation.TemplateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TemplateViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TemplateViewModel
    private lateinit var fakeRepository: FakeTemplateRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTemplateRepository()
        viewModel = TemplateViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has items from repository`() =
        runTest {
            val items = listOf(TemplateItem(1, "Title", "Desc"))
            fakeRepository.emit(items)

            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(items, state.items)
            assertEquals(false, state.isInitialLoading)
        }

    @Test
    fun `addTemplate calls repository`() =
        runTest {
            viewModel.addTemplate("New", "Desc")
            advanceUntilIdle()

            assertEquals(1, fakeRepository.addCalls)
        }

    @Test
    fun `deleteTemplate calls repository`() =
        runTest {
            viewModel.deleteTemplate(123)
            advanceUntilIdle()

            assertEquals(1, fakeRepository.deleteCalls)
            assertEquals(123, fakeRepository.lastDeletedId)
        }
}

class FakeTemplateRepository : TemplateRepository {
    private val templatesFlow = MutableStateFlow<List<TemplateItem>>(emptyList())
    var addCalls = 0
    var deleteCalls = 0
    var lastDeletedId = -1

    override fun getTemplates(): Flow<List<TemplateItem>> = templatesFlow

    override suspend fun addTemplate(
        title: String,
        description: String,
    ) {
        addCalls++
    }

    override suspend fun deleteTemplate(id: Int) {
        deleteCalls++
        lastDeletedId = id
    }

    fun emit(items: List<TemplateItem>) {
        templatesFlow.value = items
    }
}
