package com.laurentvrevin.wheris.core.location

import android.content.Context
import android.content.ContextWrapper
import android.location.Location
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.laurentvrevin.wheris.domain.location.LocationResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FusedUserLocationRepositoryTest {
    private class TestContext : ContextWrapper(null) {
        override fun getPackageName(): String = "com.laurentvrevin.wheris"

        override fun getApplicationContext(): Context = this
    }

    private class TestLocation(
        private val lat: Double,
        private val lng: Double,
        private val acc: Float? = null,
        private val alt: Double? = null,
        private val timeMs: Long = 1000L,
    ) : Location("gps") {
        override fun getLatitude(): Double = lat

        override fun getLongitude(): Double = lng

        override fun hasAccuracy(): Boolean = acc != null

        override fun getAccuracy(): Float = acc ?: 0f

        override fun hasAltitude(): Boolean = alt != null

        override fun getAltitude(): Double = alt ?: 0.0

        override fun getTime(): Long = timeMs
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
    fun `returns Success and configures CurrentLocationRequest correctly`() =
        runTest {
            val dummyContext = TestContext()
            val dummyClient = LocationServices.getFusedLocationProviderClient(dummyContext)
            val mockLocation =
                TestLocation(
                    lat = 48.8566,
                    lng = 2.3522,
                    acc = 5.0f,
                    alt = 35.0,
                    timeMs = 1000L,
                )

            var capturedPriority = -1
            var capturedGranularity = -1
            var capturedMaxUpdateAgeMillis = -1L
            var capturedDurationMillis = -1L

            val repository =
                FusedUserLocationRepository(
                    context = dummyContext,
                    fusedLocationClient = dummyClient,
                    isLocationServicesEnabled = { true },
                    fetchCurrentLocation = { _, request, _ ->
                        capturedPriority = request.priority
                        capturedGranularity = request.granularity
                        capturedMaxUpdateAgeMillis = request.maxUpdateAgeMillis
                        capturedDurationMillis = request.durationMillis
                        mockLocation
                    },
                )

            val result = repository.getCurrentLocation()

            assertTrue(result is LocationResult.Success)
            val success = result as LocationResult.Success
            assertEquals(48.8566, success.location.position.latitude, 0.0001)
            assertEquals(2.3522, success.location.position.longitude, 0.0001)
            assertEquals(5.0f, success.location.accuracyMeters!!, 0.001f)
            assertEquals(35.0, success.location.altitudeMeters!!, 0.001)
            assertEquals(1000L, success.location.timestampEpochMillis)

            assertEquals(Priority.PRIORITY_HIGH_ACCURACY, capturedPriority)
            assertEquals(Granularity.GRANULARITY_PERMISSION_LEVEL, capturedGranularity)
            assertEquals(0L, capturedMaxUpdateAgeMillis)
            assertEquals(10_000L, capturedDurationMillis)
        }

    @Test
    fun `returns Timeout when fetchLocation returns null`() =
        runTest {
            val dummyContext = TestContext()
            val dummyClient = LocationServices.getFusedLocationProviderClient(dummyContext)
            val repository =
                FusedUserLocationRepository(
                    context = dummyContext,
                    fusedLocationClient = dummyClient,
                    isLocationServicesEnabled = { true },
                    fetchCurrentLocation = { _, _, _ -> null },
                )

            val result = repository.getCurrentLocation()
            assertEquals(LocationResult.Timeout, result)
        }

    @Test
    fun `returns TechnicalError when fetchLocation throws non-cancellation exception`() =
        runTest {
            val dummyContext = TestContext()
            val dummyClient = LocationServices.getFusedLocationProviderClient(dummyContext)
            val repository =
                FusedUserLocationRepository(
                    context = dummyContext,
                    fusedLocationClient = dummyClient,
                    isLocationServicesEnabled = { true },
                    fetchCurrentLocation = { _, _, _ -> throw RuntimeException("Internal GMS error") },
                )

            val result = repository.getCurrentLocation()
            assertEquals(LocationResult.TechnicalError, result)
        }

    @Test
    fun `rethrows CancellationException and cancels cancellationTokenSource`() =
        runTest {
            val dummyContext = TestContext()
            val dummyClient = LocationServices.getFusedLocationProviderClient(dummyContext)
            var capturedCts: CancellationTokenSource? = null

            val repository =
                FusedUserLocationRepository(
                    context = dummyContext,
                    fusedLocationClient = dummyClient,
                    isLocationServicesEnabled = { true },
                    fetchCurrentLocation = { _, _, cts ->
                        capturedCts = cts
                        throw CancellationException("Operation cancelled")
                    },
                )

            try {
                repository.getCurrentLocation()
                fail("Expected CancellationException to be thrown")
            } catch (e: CancellationException) {
                assertEquals("Operation cancelled", e.message)
                assertTrue(capturedCts?.token?.isCancellationRequested == true)
            }
        }
}
