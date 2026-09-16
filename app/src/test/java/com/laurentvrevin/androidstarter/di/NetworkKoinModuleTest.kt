package com.laurentvrevin.androidstarter.di

import com.laurentvrevin.androidstarter.data.di.networkModule
import com.laurentvrevin.androidstarter.data.remote.NetworkConfig
import io.ktor.client.HttpClient
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class NetworkKoinModuleTest : KoinTest {
    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should provide NetworkConfig and HttpClient`() {
        startKoin {
            modules(configurationModule, networkModule)
        }

        val config: NetworkConfig by inject()
        val httpClient: HttpClient by inject()

        assertNotNull("NetworkConfig should be provided", config)
        assertNotNull("HttpClient should be provided", httpClient)

        httpClient.close()
    }
}
