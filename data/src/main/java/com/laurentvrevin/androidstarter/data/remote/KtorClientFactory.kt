package com.laurentvrevin.androidstarter.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClientFactory {
    /**
     * Configure le client HTTP Ktor.
     * @param engine Moteur à utiliser (OkHttp en prod, MockEngine en test).
     */
    fun create(
        engine: HttpClientEngine,
        config: NetworkConfig,
    ): HttpClient {
        return HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = config.isDebug
                        isLenient = true
                        encodeDefaults = true
                    },
                )
            }

            if (config.isDebug) {
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.INFO
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000L
                connectTimeoutMillis = 15000L
                socketTimeoutMillis = 15000L
            }

            defaultRequest {
                url(config.baseUrl)
            }
        }
    }
}
