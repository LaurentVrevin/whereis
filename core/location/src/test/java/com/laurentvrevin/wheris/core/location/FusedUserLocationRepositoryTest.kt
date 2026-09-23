package com.laurentvrevin.wheris.core.location

import android.content.Context
import android.content.ContextWrapper
import com.google.android.gms.location.LocationServices
import com.laurentvrevin.wheris.domain.location.LocationResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class FusedUserLocationRepositoryTest {
    private class TestContext : ContextWrapper(null) {
        override fun getPackageName(): String = "com.laurentvrevin.wheris"

        override fun getApplicationContext(): Context = this
    }

    @Test
    fun `returns ServicesDisabled when location services are disabled`() =
        runTest {
            val dummyContext = TestContext()
            val dummyClient = LocationServices.getFusedLocationProviderClient(dummyContext)
            val repository =
                FusedUserLocationRepository(
                    context = dummyContext,
                    fusedLocationClient = dummyClient,
                    isLocationServicesEnabled = { false },
                )

            val result = repository.getCurrentLocation()
            assertEquals(LocationResult.ServicesDisabled, result)
        }

    @Test
    fun `rethrows CancellationException without swallowing`() =
        runTest {
            val dummyContext = TestContext()
            val dummyClient = LocationServices.getFusedLocationProviderClient(dummyContext)
            val repository =
                FusedUserLocationRepository(
                    context = dummyContext,
                    fusedLocationClient = dummyClient,
                    isLocationServicesEnabled = {
                        throw CancellationException("Cancelled")
                    },
                )

            try {
                repository.getCurrentLocation()
                fail("Expected CancellationException to be thrown")
            } catch (e: CancellationException) {
                assertEquals("Cancelled", e.message)
            }
        }
}
