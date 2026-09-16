package com.laurentvrevin.androidstarter.data.base

import com.laurentvrevin.androidstarter.data.network.NetworkError
import com.laurentvrevin.androidstarter.data.network.NetworkResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

abstract class BaseRepository {
    /**
     * Exécute un appel réseau de manière sécurisée en interceptant les exceptions Ktor.
     */
    suspend fun <T> safeCall(call: suspend () -> T): NetworkResult<T> {
        return try {
            NetworkResult.Success(call())
        } catch (e: CancellationException) {
            throw e // Toujours propager l'annulation
        } catch (e: RedirectResponseException) {
            // 3xx
            NetworkResult.Error(NetworkError.Unknown("Redirect: ${e.response.status}"))
        } catch (e: ClientRequestException) {
            // 4xx
            when (e.response.status.value) {
                401 -> NetworkResult.Error(NetworkError.Unauthorized)
                403 -> NetworkResult.Error(NetworkError.Forbidden)
                404 -> NetworkResult.Error(NetworkError.NotFound)
                408 -> NetworkResult.Error(NetworkError.RequestTimeout)
                413 -> NetworkResult.Error(NetworkError.PayloadTooLarge)
                else -> NetworkResult.Error(NetworkError.Unknown(e.message))
            }
        } catch (e: ServerResponseException) {
            // 5xx
            NetworkResult.Error(NetworkError.ServerError)
        } catch (e: IOException) {
            // Network issues
            NetworkResult.Error(NetworkError.NoInternet)
        } catch (e: SerializationException) {
            NetworkResult.Error(NetworkError.Serialization)
        } catch (e: Exception) {
            NetworkResult.Error(NetworkError.Unknown(e.message))
        }
    }
}
