package com.laurentvrevin.wheris.data.repository

import com.laurentvrevin.wheris.core.datastore.WherisPreferencesDataSource
import com.laurentvrevin.wheris.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class OnboardingRepositoryImpl(
    private val preferencesDataSource: WherisPreferencesDataSource,
) : OnboardingRepository {
    override val isOnboardingCompleted: Flow<Boolean> = preferencesDataSource.isOnboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        preferencesDataSource.setOnboardingCompleted(completed)
    }
}
