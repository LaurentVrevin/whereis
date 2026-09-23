package com.laurentvrevin.wheris.core.location

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val LOCATION_TIMEOUT_MILLIS = 10_000L

class FusedUserLocationRepository(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val isLocationServicesEnabled: (Context) -> Boolean = { ctx ->
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
            lm?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    },
    private val fetchCurrentLocation: suspend (
        FusedLocationProviderClient,
        CurrentLocationRequest,
        CancellationTokenSource,
    ) -> Location? = { client, request, cts ->
        suspendCancellableCoroutine { continuation ->
            @Suppress("MissingPermission")
            val task = client.getCurrentLocation(request, cts.token)
            task.addOnSuccessListener { loc ->
                if (continuation.isActive) {
                    continuation.resume(loc)
                }
            }
            task.addOnFailureListener { exc ->
                if (continuation.isActive) {
                    continuation.resumeWithException(exc)
                }
            }
            task.addOnCanceledListener {
                if (continuation.isActive) {
                    continuation.cancel()
                }
            }
            continuation.invokeOnCancellation {
                cts.cancel()
            }
        }
    },
) : UserLocationRepository {
    override suspend fun getCurrentLocation(): LocationResult {
        if (!isLocationServicesEnabled(context)) {
            return LocationResult.ServicesDisabled
        }

        val request =
            CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                .setMaxUpdateAgeMillis(0L)
                .setDurationMillis(LOCATION_TIMEOUT_MILLIS)
                .build()

        val cancellationTokenSource = CancellationTokenSource()

        return try {
            val location =
                withTimeoutOrNull(LOCATION_TIMEOUT_MILLIS) {
                    fetchCurrentLocation(fusedLocationClient, request, cancellationTokenSource)
                }

            if (location != null) {
                LocationResult.Success(location.toUserLocation())
            } else {
                LocationResult.Timeout
            }
        } catch (e: CancellationException) {
            cancellationTokenSource.cancel()
            throw e
        } catch (e: Exception) {
            LocationResult.TechnicalError
        }
    }
}
