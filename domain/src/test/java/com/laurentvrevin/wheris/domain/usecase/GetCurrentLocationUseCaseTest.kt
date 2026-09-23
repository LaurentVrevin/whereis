package com.laurentvrevin.wheris.domain.usecase

import com.laurentvrevin.wheris.core.model.GeoPoint
import com.laurentvrevin.wheris.core.model.UserLocation
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCurrentLocationUseCaseTest {
    @Test
    fun `invoke delegates to repository with timeout`() =
        runTest {
            val expectedLocation = UserLocation(GeoPoint(48.8566, 2.3522))
            val fakeRepository =
                object : UserLocationRepository {
                    override suspend fun getCurrentLocation(timeoutMillis: Long): LocationResult {
                        assertEquals(5000L, timeoutMillis)
                        return LocationResult.Success(expectedLocation)
                    }
                }

            val useCase = GetCurrentLocationUseCase(fakeRepository)
            val result = useCase(5000L)

            assertEquals(LocationResult.Success(expectedLocation), result)
        }
}
