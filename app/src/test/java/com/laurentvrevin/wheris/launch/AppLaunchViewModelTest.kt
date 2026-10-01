package com.laurentvrevin.wheris.launch

import com.laurentvrevin.wheris.di.appLaunchModule
import com.laurentvrevin.wheris.domain.repository.OnboardingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class AppLaunchViewModelTest {
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
    fun `launch stays Loading until the preference is read`() =
        runTest(dispatcher) {
            val viewModel = AppLaunchViewModel(FakeOnboardingRepository())
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.collect {}
            }
            testScheduler.runCurrent()

            assertEquals(AppLaunchState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `incomplete onboarding requires onboarding`() =
        runTest(dispatcher) {
            val repository = FakeOnboardingRepository()
            val viewModel = AppLaunchViewModel(repository)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.collect {}
            }
            repository.setOnboardingCompleted(false)
            testScheduler.runCurrent()

            assertEquals(AppLaunchState.OnboardingRequired, viewModel.uiState.value)
        }

    @Test
    fun `completed onboarding opens main app and subsequent changes are observed`() =
        runTest(dispatcher) {
            val repository = FakeOnboardingRepository()
            val viewModel = AppLaunchViewModel(repository)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.collect {}
            }
            repository.setOnboardingCompleted(true)
            testScheduler.runCurrent()
            assertEquals(AppLaunchState.MainApp, viewModel.uiState.value)

            repository.setOnboardingCompleted(false)
            testScheduler.runCurrent()
            assertEquals(AppLaunchState.OnboardingRequired, viewModel.uiState.value)
        }

    @Test
    fun `launch module resolves the viewmodel through the domain repository`() {
        val application =
            koinApplication {
                modules(
                    appLaunchModule,
                    module { single<OnboardingRepository> { FakeOnboardingRepository() } },
                )
            }
        try {
            assertEquals(AppLaunchState.Loading, application.koin.get<AppLaunchViewModel>().uiState.value)
        } finally {
            application.close()
        }
    }

    private class FakeOnboardingRepository : OnboardingRepository {
        private val completed = MutableSharedFlow<Boolean>(replay = 1)
        override val isOnboardingCompleted: Flow<Boolean> = completed

        override suspend fun setOnboardingCompleted(completed: Boolean) {
            this.completed.emit(completed)
        }
    }
}
