package com.laurentvrevin.wheris.feature.onboarding

import com.laurentvrevin.wheris.domain.repository.OnboardingRepository
import com.laurentvrevin.wheris.feature.onboarding.di.onboardingModule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `creation does not complete onboarding`() {
        val repository = FakeRepository()
        val viewModel = OnboardingViewModel(repository)
        assertEquals(OnboardingCompletionState.Idle, viewModel.completionState.value)
        assertFalse(repository.isOnboardingCompleted.value)
        assertEquals(0, repository.calls)
    }

    @Test
    fun `completion persists true and completes only after the write`() =
        runTest(dispatcher) {
            val repository = FakeRepository()
            repository.writeGate = CompletableDeferred()
            val viewModel = OnboardingViewModel(repository)
            viewModel.complete()
            testScheduler.runCurrent()
            assertEquals(OnboardingCompletionState.Saving, viewModel.completionState.value)
            assertFalse(repository.isOnboardingCompleted.value)

            repository.writeGate?.complete(Unit)
            testScheduler.runCurrent()
            assertEquals(OnboardingCompletionState.Completed, viewModel.completionState.value)
            assertTrue(repository.isOnboardingCompleted.value)
        }

    @Test
    fun `rapid taps and subsequent taps do not duplicate writes`() =
        runTest(dispatcher) {
            val repository = FakeRepository()
            repository.writeGate = CompletableDeferred()
            val viewModel = OnboardingViewModel(repository)
            viewModel.complete()
            viewModel.complete()
            testScheduler.runCurrent()
            viewModel.complete()
            assertEquals(1, repository.calls)
            repository.writeGate?.complete(Unit)
            testScheduler.runCurrent()
            viewModel.complete()
            testScheduler.runCurrent()
            assertEquals(1, repository.calls)
        }

    @Test
    fun `write failure preserves incomplete onboarding and retry succeeds`() =
        runTest(dispatcher) {
            val repository = FakeRepository()
            repository.failWrite = true
            val viewModel = OnboardingViewModel(repository)
            viewModel.complete()
            testScheduler.runCurrent()
            assertEquals(OnboardingCompletionState.Failed, viewModel.completionState.value)
            assertFalse(repository.isOnboardingCompleted.value)

            repository.failWrite = false
            viewModel.complete()
            testScheduler.runCurrent()
            assertEquals(OnboardingCompletionState.Completed, viewModel.completionState.value)
            assertTrue(repository.isOnboardingCompleted.value)
            assertEquals(2, repository.calls)
        }

    @Test
    fun `onboarding DI uses the existing repository binding`() {
        val repository = FakeRepository()
        val application =
            koinApplication {
                modules(onboardingModule, module { single<OnboardingRepository> { repository } })
            }
        try {
            assertEquals(OnboardingCompletionState.Idle, application.koin.get<OnboardingViewModel>().completionState.value)
        } finally {
            application.close()
        }
    }

    private class FakeRepository : OnboardingRepository {
        override val isOnboardingCompleted = MutableStateFlow(false)
        var calls = 0
        var failWrite = false
        var writeGate: CompletableDeferred<Unit>? = null

        override suspend fun setOnboardingCompleted(completed: Boolean) {
            calls++
            writeGate?.await()
            if (failWrite) throw IOException("Write failed")
            isOnboardingCompleted.value = completed
        }
    }
}
