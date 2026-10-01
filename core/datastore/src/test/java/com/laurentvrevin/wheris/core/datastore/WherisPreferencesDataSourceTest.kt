package com.laurentvrevin.wheris.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class WherisPreferencesDataSourceTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `absent preference defaults to false`() =
        runTest {
            val file = temporaryFolder.newFolder().resolve("preferences.preferences_pb")
            val dataStore =
                PreferenceDataStoreFactory.create(scope = backgroundScope) { file }

            assertFalse(WherisPreferencesDataSource(dataStore).isOnboardingCompleted.first())
        }

    @Test
    fun `writes are observable including resetting completion`() =
        runTest {
            val file = temporaryFolder.newFolder().resolve("preferences.preferences_pb")
            val dataStore = PreferenceDataStoreFactory.create(scope = backgroundScope) { file }
            val source = WherisPreferencesDataSource(dataStore)
            val observed = mutableListOf<Boolean>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                source.isOnboardingCompleted.collect { observed += it }
            }
            assertFalse(source.isOnboardingCompleted.first())

            source.setOnboardingCompleted(true)
            assertTrue(source.isOnboardingCompleted.first())
            source.setOnboardingCompleted(false)
            assertFalse(source.isOnboardingCompleted.first())
            testScheduler.runCurrent()

            assertEquals(listOf(false, true, false), observed)
        }

    @Test
    fun `completion remains true when the datastore is reopened`() =
        runTest {
            val file = temporaryFolder.newFolder().resolve("preferences.preferences_pb")
            val storeJob = Job(backgroundScope.coroutineContext[Job])
            val storeScope = CoroutineScope(backgroundScope.coroutineContext + storeJob)
            val dataStore = PreferenceDataStoreFactory.create(scope = storeScope) { file }
            WherisPreferencesDataSource(dataStore).setOnboardingCompleted(true)
            storeJob.cancelAndJoin()

            val reopened = PreferenceDataStoreFactory.create(scope = backgroundScope) { file }
            assertTrue(WherisPreferencesDataSource(reopened).isOnboardingCompleted.first())
        }

    @Test
    fun `read IOException falls back to false`() =
        runTest {
            val source = WherisPreferencesDataSource(FailingDataStore(IOException("read failed")))

            assertFalse(source.isOnboardingCompleted.first())
        }

    @Test
    fun `non IO read errors propagate`() {
        val failure = IllegalStateException("read failed")
        val source = WherisPreferencesDataSource(FailingDataStore(failure))

        val thrown =
            assertThrows(IllegalStateException::class.java) {
                runBlocking { source.isOnboardingCompleted.first() }
            }
        assertEquals(failure, thrown)
    }

    @Test
    fun `write failures propagate to the caller`() {
        val failure = IOException("write failed")
        val source = WherisPreferencesDataSource(FailingDataStore(failure))

        val thrown =
            assertThrows(IOException::class.java) {
                runBlocking { source.setOnboardingCompleted(true) }
            }
        assertEquals(failure, thrown)
    }

    private class FailingDataStore(
        private val failure: Exception,
    ) : DataStore<Preferences> {
        override val data: Flow<Preferences> = flow { throw failure }

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences = throw failure
    }
}
