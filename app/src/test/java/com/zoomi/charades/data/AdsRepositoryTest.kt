package com.zoomi.charades.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AdsRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    private fun newRepository(roundsBetweenAds: Int = 3) = AdsRepository(
        PreferenceDataStoreFactory.create(scope = dataStoreScope) { File(tempFolder.root, "ads.preferences_pb") },
        roundsBetweenAds,
    )

    @Test
    fun `defaults to not due`() = runTest {
        assertFalse(newRepository().shouldShowAd.first())
    }

    @Test
    fun `recordRoundCompleted increments the counter without reaching an unmet threshold`() = runTest {
        val repository = newRepository(roundsBetweenAds = 5)

        repository.recordRoundCompleted()
        repository.recordRoundCompleted()

        assertFalse(repository.shouldShowAd.first())
    }

    @Test
    fun `shouldShowAd flips true once the injected threshold is reached`() = runTest {
        val repository = newRepository(roundsBetweenAds = 3)

        repository.recordRoundCompleted()
        repository.recordRoundCompleted()
        assertFalse(repository.shouldShowAd.first())

        repository.recordRoundCompleted()
        assertTrue(repository.shouldShowAd.first())
    }

    @Test
    fun `recordAdShown resets the counter to zero`() = runTest {
        val repository = newRepository(roundsBetweenAds = 2)

        repository.recordRoundCompleted()
        repository.recordRoundCompleted()
        assertTrue(repository.shouldShowAd.first())

        repository.recordAdShown()

        assertFalse(repository.shouldShowAd.first())
    }

    @Test
    fun `counter state survives repository re-creation against the same file`() = runTest {
        val file = File(tempFolder.root, "ads-persist.preferences_pb")

        val firstScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
        val first = AdsRepository(PreferenceDataStoreFactory.create(scope = firstScope) { file }, roundsBetweenAds = 3)
        first.recordRoundCompleted()
        first.recordRoundCompleted()
        firstScope.cancel()

        val secondScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
        val second = AdsRepository(PreferenceDataStoreFactory.create(scope = secondScope) { file }, roundsBetweenAds = 3)
        second.recordRoundCompleted()

        assertTrue(second.shouldShowAd.first())
        secondScope.cancel()
    }
}
