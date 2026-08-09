package com.zoomi.charades.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.zoomi.charades.game.WordResult
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class StatsRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    private fun newRepository() = StatsRepository(
        PreferenceDataStoreFactory.create(scope = dataStoreScope) { File(tempFolder.root, "stats.preferences_pb") },
    )

    @Test
    fun `defaults to all zero`() = runTest {
        val stats = newRepository().stats.first()

        assertEquals(0, stats.totalRoundsPlayed)
        assertEquals(0, stats.totalCorrect)
        assertEquals(0, stats.totalPassed)
        assertEquals(0, stats.bestScore)
        assertEquals(0, stats.longestStreak)
    }

    @Test
    fun `recordRound accumulates totals and computes the longest streak`() = runTest {
        val repository = newRepository()

        // correct, correct, pass, correct -> longest streak of 2
        repository.recordRound(
            deckTitle = "Movies",
            score = 3,
            results = listOf(
                WordResult("A", correct = true),
                WordResult("B", correct = true),
                WordResult("C", correct = false),
                WordResult("D", correct = true),
            ),
        )

        val stats = repository.stats.first()
        assertEquals(1, stats.totalRoundsPlayed)
        assertEquals(3, stats.totalCorrect)
        assertEquals(1, stats.totalPassed)
        assertEquals(3, stats.bestScore)
        assertEquals("Movies", stats.bestScoreDeckTitle)
        assertEquals(2, stats.longestStreak)
    }

    @Test
    fun `bestScore only updates when a later round beats it`() = runTest {
        val repository = newRepository()

        repository.recordRound("Movies", score = 5, results = List(5) { WordResult("W$it", true) })
        repository.recordRound("Animals", score = 2, results = List(2) { WordResult("W$it", true) })

        val stats = repository.stats.first()
        assertEquals(5, stats.bestScore)
        assertEquals("Movies", stats.bestScoreDeckTitle)
        assertEquals(2, stats.totalRoundsPlayed)
        assertEquals(7, stats.totalCorrect)
    }

    @Test
    fun `longestStreak keeps the best streak seen across rounds`() = runTest {
        val repository = newRepository()

        repository.recordRound("Movies", score = 2, results = listOf(WordResult("A", true), WordResult("B", true)))
        repository.recordRound(
            "Animals",
            score = 1,
            results = listOf(WordResult("C", false), WordResult("D", true)),
        )

        assertEquals(2, repository.stats.first().longestStreak)
    }
}
