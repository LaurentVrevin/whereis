package com.laurentvrevin.wheris.core.location

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.laurentvrevin.wheris.domain.location.LocationResult
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FusedUserLocationRepository(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val isLocationServicesEnabled: (Context) -> Boolean = { ctx ->
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
            lm?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    },
) : UserLocationRepository {
    override suspend fun getCurrentLocation(timeoutMillis: Long): LocationResult {
        if (!isLocationServicesEnabled(context)) {
            return LocationResult.ServicesDisabled
        }

        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val location =
                withTimeoutOrNull(timeoutMillis) {
                    suspendCancellableCoroutine<Location?> { continuation ->
                        @Suppress("MissingPermission")
                        val task =
                            fusedLocationClient.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                cancellationTokenSource.token,
                            )
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
                            cancellationTokenSource.cancel()
                        }
                    }
                }

            if (location != null) {
                LocationResult.Success(location.toUserLocation())
            } else {
                LocationResult.Timeout
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: SecurityException) {
            LocationResult.Error(e)
        } catch (e: Exception) {
            LocationResult.Error(e)
        }
    }
}
