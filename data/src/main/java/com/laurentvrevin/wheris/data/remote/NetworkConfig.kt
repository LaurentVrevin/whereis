package com.laurentvrevin.wheris.data.remote

/**
 * Configuration pour le client HTTP Ktor.
 */
data class NetworkConfig(
    val baseUrl: String,
    val isDebug: Boolean,
)
