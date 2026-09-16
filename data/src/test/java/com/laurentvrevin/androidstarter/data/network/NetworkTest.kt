package com.laurentvrevin.androidstarter.data.network

import com.laurentvrevin.androidstarter.data.base.BaseRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@Serializable
data class MockDto(val id: Int, val name: String)

class NetworkTest : BaseRepository() {
    private fun createMockClient(handler: MockRequestHandler): HttpClient {
        return HttpClient(MockEngine) {
            expectSuccess = true
            engine {
                addHandler(handler)
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun `safeCall returns Success when API call is successful`() =
        runTest {
            val client =
                createMockClient { request ->
                    respond(
                        content = """{"id": 1, "name": "Test"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val dto = client.get("test").body<MockDto>()
            assertEquals(1, dto.id)

            val result = safeCall { client.get("test").body<MockDto>() }

            if (result is NetworkResult.Error) {
                println("Test failed with error: ${result.error}")
            }
            assertTrue("Expected Success but got $result", result is NetworkResult.Success)
        }

    @Test
    fun `safeCall returns Unauthorized error on 401 response`() =
        runTest {
            val client = createMockClient { respond("", HttpStatusCode.Unauthorized) }

            val result = safeCall { client.get("test").body<MockDto>() }

            assertTrue(result is NetworkResult.Error)
            assertEquals(NetworkError.Unauthorized, (result as NetworkResult.Error).error)
        }

    @Test
    fun `safeCall returns NotFound error on 404 response`() =
        runTest {
            val client = createMockClient { respond("", HttpStatusCode.NotFound) }

            val result = safeCall { client.get("test").body<MockDto>() }

            assertTrue(result is NetworkResult.Error)
            assertEquals(NetworkError.NotFound, (result as NetworkResult.Error).error)
        }

    @Test
    fun `safeCall returns ServerError on 500 response`() =
        runTest {
            val client = createMockClient { respond("", HttpStatusCode.InternalServerError) }

            val result = safeCall { client.get("test").body<MockDto>() }

            assertTrue(result is NetworkResult.Error)
            assertEquals(NetworkError.ServerError, (result as NetworkResult.Error).error)
        }
}
